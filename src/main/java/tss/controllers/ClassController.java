package tss.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tss.annotations.session.Authorization;
import tss.annotations.session.CurrentUser;
import tss.entities.*;
import tss.exceptions.ClazzNotFoundException;
import tss.exceptions.CourseNotFoundException;
import tss.exceptions.TeacherNotFoundException;
import tss.models.Clazz;
import tss.models.ClazzList;
import tss.repositories.*;
import tss.responses.information.GetClassesResponse;

import java.util.*;

/**
 * @author reeve
 */
@RestController
@RequestMapping()
public class ClassController {
    private final CourseRepository courseRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public ClassController(CourseRepository courseRepository, ClassRepository classRepository, UserRepository
            userRepository, ClassroomRepository classroomRepository, TimeSlotRepository timeSlotRepository) {
        this.courseRepository = courseRepository;
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.classroomRepository = classroomRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @PostMapping("/courses/{courseId}/classes")
    @ResponseStatus(HttpStatus.CREATED)
    public Clazz insertClass(@PathVariable String courseId, @RequestBody Clazz clazz) {

        CourseEntity courseEntity = courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
        UserEntity userEntity = userRepository.findById(clazz.getTeacherId()).orElseThrow
                (TeacherNotFoundException::new);

        ClassEntity classEntity = new ClassEntity();
        classEntity.setYear(clazz.getYear());
        classEntity.setSemester(clazz.getSemester());
        classEntity.setCapacity(clazz.getCapacity());
        classEntity.setNumStudent(clazz.getNumStudent());
        classEntity.setTeacher(userEntity);
        classEntity.setCourse(courseEntity);

        classEntity = classRepository.save(classEntity);
        System.out.println(classEntity.getId());
        return new Clazz(classEntity);
    }

    @GetMapping(path = "/classes/{classId}")
    @ResponseStatus(HttpStatus.OK)
    public Clazz getClass(@PathVariable long classId) {
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(ClazzNotFoundException::new);
        return new Clazz(classEntity);
    }

    @DeleteMapping(path = "/classes/{classId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeClass(@PathVariable long classId) {

        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(ClazzNotFoundException::new);
        classRepository.delete(classEntity);
    }

    @PutMapping(path = "/auto-arrangement")
    @ResponseStatus(HttpStatus.OK)
    public void autoArrangement() {
        List<ClassItem> classItems = new ArrayList<>();
        for (ClassEntity classEntity : classRepository.findAll()) {
            ClassItem classItem = new ClassItem(classEntity);
            if (!classItem.hasNoLeftSection()) {
                classItems.add(classItem);
            }
        }

        for (ClassroomEntity classroomEntity : classroomRepository.findAll()) {
            if (classItems.size() == 0) {
                break;
            }
            for (TimeSlotEntity timeSlotEntity : classroomEntity.getTimeSlots()) {
                if (classItems.size() == 0) {
                    break;
                }
                final int timeSlotSize = timeSlotEntity.getType().getSize();
                for (int i = 0; i < classItems.size(); i++) {
                    ClassItem classItem = classItems.get(i);
                    if (classItem.getNumLeftSectionsOfSize(timeSlotSize) > 0) {
                        classItem.getClassEntity().addTimeSlot(timeSlotEntity);
                        timeSlotRepository.save(timeSlotEntity);
                        classItem.setNumLeftSectionsOfSize(timeSlotSize, classItem.getNumLeftSectionsOfSize(timeSlotSize) - 1);
                        if (classItem.hasNoLeftSection()) {
                            Collections.swap(classItems, i, classItems.size());
                            classItems.remove(classItems.size());
                        }
                        break;
                    }
                }
            }
        }
    }

    @GetMapping("/classes/search/findByCourse_NameAndYearAndSemester")
    @ResponseStatus(HttpStatus.OK)
    // @Authorization
    public GetClassesResponse searchClassByName(@RequestParam String courseName,
                                                @RequestParam(required = false) Integer year,
                                                @RequestParam(required = false) SemesterEnum semester) {
        if (year == null || semester == null) {
            return new GetClassesResponse(classRepository.findByCourse_Name(courseName));
        }
        return new GetClassesResponse(classRepository.findByCourse_NameAndYearAndSemester(courseName, year, semester));
    }

    @GetMapping("/classes/search/findByCourse_IdAndYearAndSemester")
    @ResponseStatus(HttpStatus.OK)
    public GetClassesResponse searchClassById(@RequestParam String courseId,
                                              @RequestParam(required = false) Integer year,
                                              @RequestParam(required = false) SemesterEnum semester) {
        if (year == null || semester == null) {
            return new GetClassesResponse(classRepository.findByCourse_Name(courseId));
        }
        return new GetClassesResponse(classRepository.findByCourse_NameAndYearAndSemester(courseId, year, semester));
    }

    @GetMapping(path = "/classes/action/search-by-course-id-no-time/{course_id}")
    // @Authorization
    public ResponseEntity<GetClassesResponse> searchClassByIdNoTime(@PathVariable String course_id) {
        Optional<CourseEntity> ret = courseRepository.findById(course_id);
        if (!ret.isPresent()) {
            return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>()),
                    HttpStatus.BAD_REQUEST);
        }
        CourseEntity course = ret.get();
        return new ResponseEntity<>(new GetClassesResponse(course.getClasses()), HttpStatus.OK); //new ResponseEntity<>(new GetClassesBySearchingNameResponse("OK", classesAll), HttpStatus.OK);
    }

    @GetMapping(path = "/classes/action/search-by-teacher/{teacher_name}/{year}/{semester}")
    // @Authorization
    public ResponseEntity<GetClassesResponse> searchClassByTeacher(@PathVariable String teacher_name,
                                                                   @PathVariable Integer year,
                                                                   @PathVariable Integer semester) {
        SemesterEnum sem;

        if (semester.equals(1)) {
            sem = SemesterEnum.FIRST;
        }
        else if (semester.equals(2)) {
            sem = SemesterEnum.SECOND;
        } else
            return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>()),
                    HttpStatus.BAD_REQUEST);

        List<UserEntity> ret = userRepository.findByName(teacher_name);

        if (ret.isEmpty()) {
            return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>()),
                    HttpStatus.BAD_REQUEST);
        }

        List<ClassEntity> classesAll = new ArrayList<>();
        for (UserEntity teacher : ret) {
            if (!teacher.readTypeName().equals("Teacher")) {
                continue;
            }
            List<ClassEntity> classes = teacher.getClassesTeaching();
            for (ClassEntity clazz : classes) {
                if (clazz.getYear().equals(year) && clazz.getSemester().equals(sem)) {
                    classesAll.add(clazz);
                }
            }
        }

        return new ResponseEntity<>(new GetClassesResponse(classesAll), HttpStatus.OK);
    }

    @GetMapping(path = "/classes/action/search-by-teacher-no-time/{teacher_name}")
    // @Authorization
    public ResponseEntity<GetClassesResponse> searchClassByTeacherNoTime(// @CurrentUser UserEntity user,
                                                                                @PathVariable String teacher_name) {
        List<UserEntity> ret = userRepository.findByName(teacher_name);

        if (ret.isEmpty()) {
            return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>()),
                    HttpStatus.BAD_REQUEST);
        }

        List<ClassEntity> classesAll = new ArrayList<>();
        for (UserEntity teacher : ret) {
            if (!teacher.readTypeName().equals("Teacher")) {
                continue;
            }
            classesAll.addAll(teacher.getClassesTeaching());
        }

        return new ResponseEntity<>(new GetClassesResponse(classesAll), HttpStatus.OK);
    }

    @GetMapping(path = "/classes/action/search-both/{course_name}/{teacher_name}/{year}/{semester}")
    // @Authorization
    public ResponseEntity<GetClassesResponse> searchClassBoth(// @CurrentUser UserEntity user
                                             @PathVariable String course_name,
                                             @PathVariable String teacher_name,
                                             @PathVariable Integer year,
                                             @PathVariable Integer semester
                                          ) {
        SemesterEnum sem;
        if (semester.equals(1)) {
            sem = SemesterEnum.FIRST;
        }
        else if (semester.equals(2)) {
            sem = SemesterEnum.SECOND;
        } else
            return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>()),
                    HttpStatus.BAD_REQUEST);

        List<CourseEntity> ret = courseRepository.findByName(course_name);
        List<UserEntity> retu = userRepository.findByName(teacher_name);

        if (ret.isEmpty() || retu.isEmpty()) {
            return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>()),
                    HttpStatus.BAD_REQUEST);
        }

        Set<ClassEntity> classesByCourseName = new HashSet<>();
        Set<ClassEntity> classesByTeacherName = new HashSet<>();

        for (CourseEntity course : ret) {
            List<ClassEntity> classes = course.getClasses();

            for (ClassEntity clazz : classes) {
                if (clazz.getYear().equals(year)) {
                    classesByCourseName.add(clazz);
                }
            }
        }

        for (UserEntity teacher : retu) {
            if (!teacher.readTypeName().equals("Teacher")) {
                continue;
            }
            List<ClassEntity> classes = teacher.getClassesTeaching();

            for (ClassEntity clazz : classes) {
                if (clazz.getYear().equals(year) && clazz.getSemester().equals(sem)) {
                    classesByTeacherName.add(clazz);
                }
            }
        }
        System.out.println("classesByCourseName" + classesByCourseName.size());
        System.out.println("classesByTeacherName" + classesByTeacherName.size());

        Set<ClassEntity> res = new HashSet<>();
        res.clear();
        res.addAll(classesByCourseName);
        res.retainAll(classesByTeacherName);

        return new ResponseEntity<>(new GetClassesResponse(new ArrayList<>(res)), HttpStatus.OK);

    }


    @PutMapping(path = "/classes/action/select/{classId}")
    @Authorization
    @ResponseStatus(HttpStatus.OK)
    public void registerClass(@CurrentUser UserEntity user,
                              @PathVariable long classId) {
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(ClazzNotFoundException::new);


    }


}

/**
 * Auxiliary class for auto-arrangement of classes.
 */
class ClassItem {
    private long classId;
    private int numLeftSectionsOfSizeTwo = 0;
    private int numLeftSectionsOfSizeThree = 0;
    private ClassEntity classEntity;

    ClassItem(ClassEntity classEntity) {
        switch (classEntity.getCourse().getNumLessonsEachWeek()) {
            case 2:
                numLeftSectionsOfSizeTwo = 1;
                break;
            case 3:
                numLeftSectionsOfSizeThree = 1;
                break;
            case 4:
                numLeftSectionsOfSizeTwo = 2;
                break;
            case 5:
                numLeftSectionsOfSizeTwo = 1;
                numLeftSectionsOfSizeThree = 1;
                break;
            case 6:
                numLeftSectionsOfSizeThree = 2;
            default:
        }
        classId = classEntity.getId();
        this.classEntity = classEntity;
    }

    int getNumLeftSectionsOfSize(int size) {
        switch (size) {
            case 2:
                return numLeftSectionsOfSizeTwo;
            case 3:
                return numLeftSectionsOfSizeThree;
            default:
                return 0;
        }
    }

    void setNumLeftSectionsOfSize(int size, int value) {
        switch (size) {
            case 2:
                numLeftSectionsOfSizeTwo = value;
                break;
            case 3:
                numLeftSectionsOfSizeThree = value;
                break;
            default:
        }
    }

    boolean hasNoLeftSection() {
        return numLeftSectionsOfSizeTwo + numLeftSectionsOfSizeThree == 0;
    }

    long getClassId() {
        return classId;
    }

    void setClassId(long classId) {
        this.classId = classId;
    }

    ClassEntity getClassEntity() {
        return classEntity;
    }

    void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }
}