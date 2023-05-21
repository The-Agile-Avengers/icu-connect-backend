package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.*;
import com.agileavengers.icuconnectbackend.model.*;
import com.agileavengers.icuconnectbackend.model.dto.*;
import com.agileavengers.icuconnectbackend.repository.*;
import com.agileavengers.icuconnectbackend.service.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class CommunityService implements ICommunityService {

    private final CommunityMapper communityMapper;
    private final RatingMapper ratingMapper;
    private final InstructorMapper instructorMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    PostRepository postRepository;
    CommunityRepository communityRepository;
    InstructorRepository instructorRepository;
    RatingRepository ratingRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository,
            InstructorRepository instructorRepository, RatingRepository ratingRepository,
            UserRepository userRepository, CommunityMapper communityMapper, RatingMapper ratingMapper,
            InstructorMapper instructorMapper, PostRepository postRepository, PostMapper postMapper,
            CommentMapper commentMapper, CommentRepository commentRepository) {
        this.communityRepository = communityRepository;
        this.instructorRepository = instructorRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.communityMapper = communityMapper;
        this.ratingMapper = ratingMapper;
        this.instructorMapper = instructorMapper;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    /**
     * Create a new community based on received dto.
     * Creates a new instructor object if instructor with given name cannot be found.
     *
     * @param communityDto Community to be created
     * @return Created community
     */
    @Override
    public CommunityDto createCommunity(CommunityDto communityDto) {
        Community community = new Community();
        if (communityDto.getModuleId() != null) {
            if (communityRepository.findCommunityByModuleId(communityDto.getModuleId())
                    .isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Module ID already exists");
            }
            community.setModuleId(communityDto.getModuleId());
        }
        if (communityDto.getInstructor() != null) {
            Optional<Instructor> instructor = instructorRepository
                    .findInstructorByName(communityDto.getInstructor().getName());
            if (instructor.isPresent()) {
                community.setInstructor(instructor.get());
            } else {
                community.setInstructor(instructorRepository.save(
                        instructorMapper.fromDto(communityDto.getInstructor())));
            }
        }
        community.setName(communityDto.getName());
        community.setEcts(communityDto.getEcts());
        return communityMapper.toDto(communityRepository.save(community));
    }

    /**
     * Get a page of communities.
     * If search is given, it will filter for communities that contain the term in either moduleId, instructor or name.
     * @param page page index
     * @param size number of communities per page
     * @param search term to filter for
     * @return page of communites
     */
    @Override
    public Page<CommunityDto> getCommunities(int page, int size, Optional<String> search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Community> communityPage;
        if (search.isPresent()) {
            String query = search.get();
            communityPage = communityRepository
                    .findAllByNameContainingOrModuleIdContainingOrInstructor_NameContaining(pageable, query, query, query);
        } else {
            communityPage = communityRepository.findAll(pageable);
        }
        return communityPage.map(communityMapper::toDto);
    }

    /**
     * Get a community by an id. Throws an exception if no community with moduleId exists.
     * @param moduleId id of the community
     * @return community with moduleId
     */
    @Override
    public CommunityDto getCommunity(String moduleId) {
        Optional<Community> communityOptional = this.communityRepository.findCommunityByModuleId(moduleId);
        if (communityOptional.isPresent()) {
            return communityMapper.toDto(communityOptional.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
    }

    /**
     * Get page of ratings related to a community. If sortByMostLiked is true or not given, it will be sorted by the highest count of likes. Otherwise it will be sorted by most recent.
     *
     * @param moduleId id of community
     * @param page page index
     * @param size number of ratings per page
     * @param sortByMostLiked bool to change sorting
     * @return page of ratings
     */
    @Override
    public Page<RatingDto> getCommunityRatings(String moduleId, int page, int size, Optional<Boolean> sortByMostLiked) {
        Sort sort = Sort.by("creation").descending();
        if (sortByMostLiked.isPresent() && sortByMostLiked.get()) {
            sort = Sort.by("thumbsUpCount").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        Page<Rating> ratingPage = ratingRepository.findAllByCommunity_ModuleId(moduleId, pageable);
        return ratingPage.map(ratingMapper::toDto);
    }

    /**
     * Method to like or unlike a rating. If the logged in user has already liked the rating, it will be unliked.
     * @param moduleId id of the community
     * @param ratingId id of the rating that will be liked or unliked
     * @param username username of logged in user
     * @return updated rating object.
     */
    @Override
    public RatingDto thumbsUp(String moduleId, Long ratingId, String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }

        Optional<Rating> ratingOptional = ratingRepository.findByIdAndCommunity_ModuleId(ratingId, moduleId);
        if (ratingOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating does not exist");
        }

        User user = userOptional.get();
        Rating rating = ratingOptional.get();

        return ratingMapper.toDto(ratingRepository.save(rating.modifyThumbsUp(user)));
    }

    /**
     * Create a new rating for a community. If the user has already rated, an exception will be thrown.
     * @param moduleId id of the community to rate
     * @param ratingDto information about the rating
     * @param username logged in username
     * @return created rating
     */
    @Override
    public RatingDto createCommunityRating(String moduleId, RatingDto ratingDto, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        if (ratingRepository.findByCommunity_ModuleIdAndCreator_Id(community.get().getModuleId(), user.get().getId())
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has already rated this community");
        }
        if (ratingDto.getTeaching() == null || ratingDto.getContent() == null
                || ratingDto.getWorkload() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "not all categories were rated");
        }
        if (ratingDto.getTeaching() < 0.0 || ratingDto.getTeaching() > 5.0
                || ratingDto.getWorkload() < 0.0 || ratingDto.getWorkload() > 5.0
                || ratingDto.getContent() < 0.0 || ratingDto.getContent() > 5.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ratings are not within range");
        }

        Rating rating = ratingMapper.fromDto(ratingDto);
        rating.setCommunity(community.get());
        rating.setCreator(user.get());
        rating.setCreation(new Timestamp(System.currentTimeMillis()));
        rating.setThumbsUp(Collections.<User>emptySet());

        return ratingMapper.toDto(ratingRepository.save(rating));
    }

    /**
     * Method to calculate the average rating of a community
     * @param moduleId id of the community
     * @return obejct containing calculated average
     */
    @Override
    public RatingAverage getCommunityRatingAverage(String moduleId) {
        List<Rating> ratingList = ratingRepository.findAllByCommunity_ModuleId(moduleId);
        return new RatingAverage(ratingList);
    }

    /**
     * Deletes a community. Currently no restrictions, thus no endpoint provided
     * @param moduleId id of the community
     */
    @Override
    public void deleteCommunity(String moduleId) {
        // TODO: make sure requesting user has rights to do so

        if (communityRepository.existsByModuleId(moduleId)) {
            communityRepository.deleteCommunityByModuleId(moduleId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    /**
     * Create a new post related to a community
     * @param moduleId id of the community
     * @param postDto object containing information about the post
     * @param username of user creating the post
     * @return created post
     */
    @Override
    public PostDto createPost(String moduleId, PostDto postDto, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }

        Post post = postMapper.fromDto(postDto);
        post.setCreator(user.get());

        post.setCreation(new Timestamp(System.currentTimeMillis()));

        post.setCommunity(community.get());

        return postMapper.toDto(postRepository.save(post));
    }

    /**
     * Method returning a page of posts related to a community
     * @param moduleId id of the community
     * @param pageNumber page index
     * @param size number of ratings per page
     * @param year if given, filters the posts to only be from that year
     * @return page of filtered posts
     */
    @Override
    public Page<PostDto> getCommunityPosts(String moduleId, int pageNumber, int size, Optional<Integer> year) {
        Pageable page = PageRequest.of(pageNumber, size);
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        Page<Post> postPage;
        if (year.isPresent()) {
            postPage = postRepository.findAllByCommunity_ModuleIdAndCreationBetween(moduleId, Timestamp.valueOf(year.get() + "-01-01 00:00:00.0"), Timestamp.valueOf(year.get() + "-12-31 23:59:59.9"), page);
        } else {
            postPage = postRepository.findAllByCommunity_ModuleId(moduleId, page);
        }
        return postPage.map(postMapper::toDto);
    }

    /**
     * Delete a post. Only allowed if the logged in user created the post. Otherwise an exception is thrown.
     * @param moduleId id of the community
     * @param postId id of the post to be deleted
     * @param username of the logged in user
     */
    @Override
    public void deletePost(String moduleId, Long postId, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }

        Optional<Post> post = postRepository.findByIdAndCommunity_ModuleId(postId, moduleId);
        if (post.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }   

        if (!post.get().getCreator().equals(user.get())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized to delete this post. This post was created by another user.");
        }

        List<Comment> commentsToDelete = commentRepository.findAllByPost_Id(postId);
        for (Comment comment : commentsToDelete) {
            commentRepository.delete(comment);
        }
        postRepository.delete(post.get());
    }

    /**
     * Create a new comment related to a post. Exceptions are thrown if the community or the psot does not exist.
     * @param moduleId id of the community
     * @param postId id of the post
     * @param commentDto object containing information about the comment to be added
     * @param username of the user creating the post
     * @return created comment
     */
    @Override
    public CommentDto createComment(String moduleId, Long postId, CommentDto commentDto, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        Optional<Post> post = postRepository.findByIdAndCommunity_ModuleId(postId, moduleId);
        if (post.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "post does not exist");
        }

        Comment comment = commentMapper.fromDto(commentDto);
        comment.setCreator(user.get());
        comment.setPost(post.get());
        comment.setCreation(new Timestamp(System.currentTimeMillis()));

        return commentMapper.toDto(commentRepository.save(comment));
    }
}
