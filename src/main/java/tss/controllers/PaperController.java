package tss.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import tss.entities.PaperContainsQuestionEntity;
import tss.entities.PapersEntity;
import tss.entities.QuestionEntity;
import tss.information.PaperResponseStruct;
import tss.repositories.PaperContainsQuestionRepository;
import tss.repositories.PaperRepository;
import tss.repositories.QuestionRepository;
import tss.requests.information.AddPaperRequest;
import tss.requests.information.DeletePaperRequest;
import tss.requests.information.GetPaperRequest;
import tss.requests.information.ModifyPaperRequest;
import tss.responses.information.AddPaperResponse;
import tss.responses.information.DeletePaperResponse;
import tss.responses.information.GetPaperResponse;
import tss.responses.information.ModifyPaperResponse;
import tss.utils.RandomSet;

import java.util.*;

@Controller
@RequestMapping(path = "testsys_paper")
public class PaperController {
    private final PaperRepository paperRepository;
    private final QuestionRepository questionRepository;
    private final PaperContainsQuestionRepository paperContainsQuestionRepository;

    @Autowired
    public PaperController(PaperRepository paperRepository, QuestionRepository questionRepository, PaperContainsQuestionRepository paperContainsQuestionRepository){
        this.paperRepository = paperRepository;
        this.questionRepository = questionRepository;
        this.paperContainsQuestionRepository = paperContainsQuestionRepository;
    }

    @PostMapping(path = "/insert")
    public ResponseEntity<AddPaperResponse> addPaper(@RequestBody AddPaperRequest request){  //用jason传过来的请求体
        if(paperRepository.existsById(request.getPid())){
            System.out.println("Duplicate pid");
            return new ResponseEntity<>(new AddPaperResponse("Failed with duplicate pid", ""), HttpStatus.BAD_REQUEST);
        }//处理重复pid的问题

        //如果不重复就创建新的paper
        PapersEntity paper = new PapersEntity();
        Set<PaperContainsQuestionEntity> paperquestions = new HashSet<>();



        paper.setPid(request.getPid());
        paper.setPapername(request.getPapername());
        paper.setIsauto(request.getIsauto());
        paper.setBegin(request.getBegin());
        paper.setEnd(request.getEnd());
        paper.setLast(request.getLast());

      //  paper.setPaperquestion();!!!!

        paper.setAnswerednum(0);
        paper.setAverage(0.0);

        if(!request.getIsauto()){// 如果不是自动生成试卷
            paper.setCount(request.getCount());

            for(int i = 0; i < Integer.valueOf(request.getCount()); i++){
                PaperContainsQuestionEntity contain = new PaperContainsQuestionEntity();

                //request中的pid和score本身是字符串，每次读取相应的id和score，id需要进行一下计算
                int tempid = Integer.valueOf(request.getPid()) * 10000 + i;
                contain.setId(String.valueOf(tempid));
                contain.setPaper(paper);
                contain.setScore(request.getScore()[i]);

                //setQuestion
                Optional<QuestionEntity> ret = questionRepository.findById(request.getQid()[i]);
                if(!ret.isPresent()){
                    System.out.println("non-exist qid");
                    return new ResponseEntity<>(new AddPaperResponse("non-exist qid", paper.getPid()), HttpStatus.BAD_REQUEST);
                }
                QuestionEntity question = ret.get();

                contain.setQuestion(question);

                paperquestions.add(contain);
                paperContainsQuestionRepository.save(contain);

            }
        }else{
            paper.setCount(request.getPcount());



            //将该单元的题目库一一对应
            HashMap questions = new HashMap();
            Iterable<QuestionEntity>question_find = questionRepository.findAll();
            int count_t = 0;
            for(QuestionEntity question: question_find){
               // questions.add(question);
                if(question.getQunit().equals(request.getPunit())){
                    questions.put(count_t, question);
                    count_t ++;
                }
            }


            //得到随机的数列
            HashSet<Integer> set = new HashSet<>();
            int max = count_t - 1;
            int n = Integer.valueOf(request.getPcount());
            RandomSet.randomSet(0, max, n, set);

            System.out.println("setsize:"+set.size());
            System.out.println("n:"+n);
            //对每一个随机数添加进contain表格
            Iterator it = set.iterator();
            int index;
            for(int i = 0; i < n; i++){
                if(it.hasNext()){
                    index = (int)it.next();
                //    System.out.println(i +"th: "+index);
                    PaperContainsQuestionEntity contain = new PaperContainsQuestionEntity();

                    int tempid = Integer.valueOf(request.getPid()) * 10000 + i;
                    contain.setId(String.valueOf(tempid));
                    contain.setPaper(paper);

                    int tempscore = 100/n;
                    contain.setScore(String.valueOf(tempscore));

                    //添加question
                    QuestionEntity question = (QuestionEntity)questions.get(i);
                    contain.setQuestion(question);

          //          System.out.println("qid:"+question.getQid());
                    paperquestions.add(contain);
                    paperContainsQuestionRepository.save(contain);
                }
                else{
                    System.out.println("Set is not full! "+i);
                    break;
                }
            }
        }


        paper.setPaperquestion(paperquestions);

        paperRepository.save(paper);
        System.out.println("insert paper"+request.getPid());

        return new ResponseEntity<>(new AddPaperResponse("ok", paper.getPid()), HttpStatus.CREATED);

    }

    @PostMapping (path = "/delete")
    public ResponseEntity<DeletePaperResponse>deletePaper(@RequestBody DeletePaperRequest request){
        String pid = request.getPid();
        if(!paperRepository.existsById(pid)){
            return new ResponseEntity<>(new DeletePaperResponse("Paper does not exist"), HttpStatus.BAD_REQUEST);
        }
        paperRepository.deleteById(pid);

        return new ResponseEntity<>(new DeletePaperResponse("ok"), HttpStatus.OK);
    }


    @PostMapping(path = "/update")
    public ResponseEntity<ModifyPaperResponse>modifyPaper(@RequestBody ModifyPaperRequest request){
        String pid = request.getPid();
        Optional<PapersEntity> ret = paperRepository.findById(pid);
        if(!ret.isPresent()){
            System.out.println("Paper does not exist:"+request.getPid());
            return new ResponseEntity<>(new ModifyPaperResponse("Paper does not exist", request.pid), HttpStatus.BAD_REQUEST);
        }
        PapersEntity paper = ret.get();//先判断是否存在卷子，如果存在，就把找到的paper从option放回 entity类型


   //     paperContainsQuestionRepository.deleteByPaper(paper);

        //删除旧的
        List<PaperContainsQuestionEntity> contain_find = paperContainsQuestionRepository.findByPaper(paper);
        for(PaperContainsQuestionEntity contain_t : contain_find){
           paperContainsQuestionRepository.deleteById(contain_t.getId());
        }

        Set<PaperContainsQuestionEntity> paperquestions = new HashSet<>();

        paper.setPid(request.getPid());
        paper.setPapername(request.getPapername());
        paper.setIsauto(request.getIsauto());
        paper.setBegin(request.getBegin());
        paper.setEnd(request.getEnd());
        paper.setLast(request.getLast());
        paper.setCount(request.getCount());

        paper.setAnswerednum(0);
        paper.setAverage(0.0);

        System.out.println("count:"+request.getCount());


        for(int i = 0; i < Integer.valueOf(request.getCount()); i++){
            PaperContainsQuestionEntity contain = new PaperContainsQuestionEntity();

            int tempid = Integer.valueOf(request.getPid()) * 10000 + i;
            contain.setId(String.valueOf(tempid));
            contain.setPaper(paper);
            contain.setScore(request.getScore()[i]);

            //setQuestion
            Optional<QuestionEntity> ret2 = questionRepository.findById(request.getQid()[i]);
            if(!ret2.isPresent()){
                return new ResponseEntity<>(new ModifyPaperResponse("non-exist qid", paper.getPid()), HttpStatus.BAD_REQUEST);
            }
            QuestionEntity question = ret2.get();

            contain.setQuestion(question);

            paperquestions.add(contain);
            paperContainsQuestionRepository.save(contain);

        }

        paper.setPaperquestion(paperquestions);

        paperRepository.save(paper);
        System.out.println("update paper"+request.getPid());


        return new ResponseEntity<>(new ModifyPaperResponse("ok", request.pid), HttpStatus.OK);
    }

    @PostMapping(path = "/search")
    public ResponseEntity<GetPaperResponse>searchPaper(@RequestBody GetPaperRequest request){
        List<PaperResponseStruct> papers = new ArrayList<>();

        String type = request.getDirection();

        String[] qid;
        String[] score;

        if(type.equals("all")){
            Iterable<PapersEntity> paper_find = paperRepository.findAll();
            for(PapersEntity paper : paper_find){
                PaperResponseStruct paper_return = new PaperResponseStruct();
                paper_return.setPid(paper.getPid());
                paper_return.setBegin(paper.getBegin());
                paper_return.setEnd(paper.getEnd());
                paper_return.setCount(paper.getCount());
                paper_return.setIsauto(paper.getIsauto());
                paper_return.setLast(paper.getLast());
                paper_return.setPapername(paper.getPapername());


                int count = 0;


                List<PaperContainsQuestionEntity> contain_find = paperContainsQuestionRepository.findByPaper(paper);
                qid = new String[contain_find.size()];
                score = new String[contain_find.size()];

                for(PaperContainsQuestionEntity contain:contain_find){
                     qid[count] = contain.getQuestion().getQid();
                     score[count] = contain.getScore();
                     count++;
             //   System.out.println(contain.getId());
              //  System.out.println(contain.getQuestion().getQid());
                }

                paper_return.setQid(qid);
                paper_return.setScore(score);

                papers.add(paper_return);
            }

        }
        else{
            System.out.println("Invalid direction: "+type);
            return new ResponseEntity<>(new GetPaperResponse("Invalid direction", papers), HttpStatus.BAD_REQUEST);
        }


        System.out.println("search: "+type);
        return new ResponseEntity<>(new GetPaperResponse("ok", papers), HttpStatus.OK);
    }
}
