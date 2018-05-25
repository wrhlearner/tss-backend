package tss.controllers.bbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.rmi.log.LogInputStream;
import tss.annotations.session.Authorization;
import tss.annotations.session.CurrentUser;
import tss.entities.UserEntity;
import tss.entities.bbs.BbsSectionEntity;
import tss.entities.bbs.BbsTopicEntity;
import tss.repositories.UserRepository;
import tss.repositories.bbs.BbsSectionRepository;
import tss.repositories.bbs.BbsTopicRepository;
import tss.requests.information.bbs.*;
import tss.responses.information.bbs.*;

import java.text.DateFormat;
import java.util.*;

@Controller
@RequestMapping(path = "/topic")
public class BbsTopicController {
    private final BbsSectionRepository bbsSectionRepository;
    private final UserRepository userRepository;
    private final BbsTopicRepository bbsTopicRepository;

    @Autowired
    public BbsTopicController(BbsSectionRepository bbsSectionRepository,
                              UserRepository userRepository,
                              BbsTopicRepository bbsTopicRepository){
        this.bbsSectionRepository = bbsSectionRepository;
        this.userRepository = userRepository;
        this.bbsTopicRepository = bbsTopicRepository;
    }

    /* create a topic
     * request: id, name, section id, content
     * permission: anyone login
     * return: id, name, content, time
     */
    @PostMapping(path = "/add")
    @Authorization
    public ResponseEntity<AddBbsTopicResponse> addBbsTopic(@CurrentUser UserEntity user,
                                                           @RequestBody AddBbsTopicRequest request){
        /* every topic bind to a section
         * invalid section id error
         */
        long sectionId = request.getSectionId();
        Optional<BbsSectionEntity> ret = bbsSectionRepository.findById(sectionId);
        if(!ret.isPresent())
            return new ResponseEntity<>(new AddBbsTopicResponse("invalid section id", -1, null, null, null), HttpStatus.BAD_REQUEST);

        /* anyone login got the permission
         * bind the author & belongedSectionId
         */
        BbsTopicEntity topic = new BbsTopicEntity(user, ret.get());

        /* init id & name & content */
        long topicId = request.getId();
        String name = request.getName();
        String content = request.getContent();
        topic.setContent(content);
        topic.setName(name);
        topic.setId(topicId);

        /* init timestamp */
        Date date = new Date();
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        mediumDateFormat.format(date);
        topic.setTime(date);

        /* reply num */
        bbsTopicRepository.save(topic);
        return new ResponseEntity<>(new AddBbsTopicResponse("ok", topicId, name, content, date), HttpStatus.OK);
    }

    /* delete by id
     * request: id
     * permission : manager, author
     * response: id, name, author name;
     */
    @DeleteMapping(path = "delete")
    @Authorization
    public ResponseEntity<DeleteBbsTopicResponse> deleteBbsTopic(@CurrentUser UserEntity user,
                                                                 @RequestBody DeleteBbsTopicRequest request){
        /* invalid topic id error */
        Optional<BbsTopicEntity> ret = bbsTopicRepository.findById(request.getId());
        if(!ret.isPresent())
            return new ResponseEntity<>(new DeleteBbsTopicResponse("no such topic id", -1, null, null), HttpStatus.BAD_REQUEST);

        BbsTopicEntity topic = ret.get();

        /* only author and manager get the permission */
        if(user.getType() != UserEntity.TYPE_MANAGER
                || !user.getUid().equals(topic.getAuthor().getUid()))
            return new ResponseEntity<>(new DeleteBbsTopicResponse("permission denied", -1, null, null), HttpStatus.FORBIDDEN);

        String authorName = topic.getAuthor().getName();
        String topicName = topic.getName();
        bbsTopicRepository.delete(topic);

        return new ResponseEntity<>(new DeleteBbsTopicResponse("ok", request.getId(), topicName, authorName), HttpStatus.OK);
    }

    /* modify the topic content
     * request: id, new content
     * permission: author only
     * return: id, name, content, time
     */
    @PostMapping(path = "modify")
    @Authorization
    public ResponseEntity<ModifyTopicContentResponse> modifyTopicContent(@CurrentUser UserEntity user,
                                                                         @RequestBody ModifyTopicContentRequest request){
        /* invalid topic id error */
        Optional<BbsTopicEntity> ret = bbsTopicRepository.findById(request.getId());
        if(!ret.isPresent())
            return new ResponseEntity<>(new ModifyTopicContentResponse("no such topic", -1, null, null, null), HttpStatus.BAD_REQUEST);
        BbsTopicEntity topic = ret.get();

        /* not author, error */
        if(!user.getUid().equals(topic.getAuthor().getUid()))
            return new ResponseEntity<>(new ModifyTopicContentResponse("permission denied", -1, null, null, null), HttpStatus.FORBIDDEN);

        /* modify content by new content */
        topic.setContent(request.getNewContent());

        Date date = new Date();
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        mediumDateFormat.format(date);
        topic.setTime(date);

        bbsTopicRepository.save(topic);
        return new ResponseEntity<>(new ModifyTopicContentResponse("ok", topic.getId(), topic.getName(), topic.getContent(), topic.getTime()), HttpStatus.OK);
    }

    /* Look for topic by id
     * get par: id
     * permission: anyone
     * return: id, name, content, time, author_name, section_name, reply number
     */
    @GetMapping(path = "id")
    public ResponseEntity<GetTopicInfoByIdResponse> getTopicInfoById(@RequestParam long id){
        /* invalid topic id error */
        Optional<BbsTopicEntity> ret = bbsTopicRepository.findById(id);
        if(!ret.isPresent())
            return new ResponseEntity<>(new GetTopicInfoByIdResponse("no such topic", -1, null, null, null,
                    null, null, 0), HttpStatus.BAD_REQUEST);

        BbsTopicEntity topic = ret.get();
        String name = topic.getName();
        String content = topic.getContent();
        String authorName = topic.getAuthor().getName();
        String sectionName = topic.getBelongedSection().getName();
        Date time = topic.getTime();
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        mediumDateFormat.format(time);
        int replyNum = topic.getReplyNum();

        return new ResponseEntity<>(new GetTopicInfoByIdResponse("ok", topic.getId(), name, content,
                                    time, authorName, sectionName, replyNum), HttpStatus.OK);
    }

    /* published topics by current user
     * v1.0, done
     */
    @GetMapping(path = "/published")
    public ResponseEntity<GetUserPublishedResponse> getUserPublished(@CurrentUser UserEntity user,
                                                                     @RequestParam String page){
        Optional<List<BbsTopicEntity>> ret = bbsTopicRepository.findByAuthor(user);
        if(!ret.isPresent())
            return new ResponseEntity<>(new GetUserPublishedResponse(null,null,null,null, null,null,null,null), HttpStatus.BAD_REQUEST);

        List<BbsTopicEntity> topics = ret.get();

        String userName = user.getName();
        String currentPage = page;
        String totalPage = String.valueOf(topics.size()/20); // +1?
        List<String> titles = new ArrayList<>();
        List<String> times = new ArrayList<>();
        List<String> topicIDs = new ArrayList<>();
        List<String> boardIDs = new ArrayList<>();
        List<String> boardNames = new ArrayList<>();

        Iterator<BbsTopicEntity> iter = topics.iterator();
        int count = 0;
        while(iter.hasNext()){
            count++;
            if(count < (Integer.valueOf(page)-1)*20 + 1)
                continue;
            if(count > (Integer.valueOf(page)*20))
                break;

            BbsTopicEntity topic = iter.next();
            titles.add(topic.getName());
            topicIDs.add(String.valueOf(topic.getId()));
            boardNames.add(topic.getBelongedSection().getName());
            boardIDs.add(String.valueOf(topic.getBelongedSection().getId()));
            times.add(topic.getTime().toString());
        }

        return new ResponseEntity<>(new GetUserPublishedResponse(userName, titles, times, topicIDs, boardIDs, boardNames, currentPage, totalPage), HttpStatus.BAD_REQUEST);
    }


    /* show all topics under a certain section
     * public part/ top part
     * v1.0, done
     */
    @PostMapping(path = "/topinfo")
    public ResponseEntity<GetAllTopicsPublicResponse> getAllTopTopics(@CurrentUser UserEntity user,
                                                                   @RequestBody GetAllTopicsPublicRequest request){
        /* haven't deal with not found situation */
        Optional<BbsSectionEntity> sret = bbsSectionRepository.findById(Long.valueOf(request.getBoardID()));
        if(!sret.isPresent())
            return new ResponseEntity<>(new GetAllTopicsPublicResponse(null, null,null,null,null,null,null,null,null), HttpStatus.BAD_REQUEST);

        BbsSectionEntity section = sret.get();

        String boardName = section.getName();
        String boardID = String.valueOf(section.getId());
        String boardText = section.getNotice();

        List<String> topTitles = new ArrayList<>();
        List<String> topAuthors = new ArrayList<>();
        List<String> topTimes = new ArrayList<>();
        List<String> topReplys = new ArrayList<>();
        List<String> topTopicIDs = new ArrayList<>();
        List<String> topLastReplyTimes = new ArrayList<>();

        Set<BbsTopicEntity> topics = section.getTopics();
        for(BbsTopicEntity topic : topics){
            /* find topic been set top */
            if(topic.isTop()){
                topTitles.add(topic.getName());
                topAuthors.add(topic.getAuthor().getName());
                topTimes.add(topic.getTime().toString());
                topReplys.add(String.valueOf(topic.getReplyNum()));
                topTopicIDs.add(String.valueOf(topic.getId()));
                topLastReplyTimes.add(topic.getLastReplyTime().toString());
            }
        }
        return new ResponseEntity<>(new GetAllTopicsPublicResponse(boardName,boardID,boardText,topTitles,topAuthors,topTimes,topReplys,topTopicIDs,topLastReplyTimes), HttpStatus.OK);
    }

    /* show all topics under a certain section
     * page show, / not top part
     * v1.0,
     */
    @PostMapping(path = "/info")
    public ResponseEntity<GetAllNotTopTopicsResponse> getAllNotTopTopics(@CurrentUser UserEntity user,
                                               @RequestBody GetAllNotTopTopicsRequest request){
        
    }


}
