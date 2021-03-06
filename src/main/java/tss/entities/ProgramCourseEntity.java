package tss.entities;


import javax.persistence.*;

/**
 * @Author zengzx
 */

@Entity
@Table(name = "program_course")
public class ProgramCourseEntity {
    public static final int COMPULSORY_COURSE = 0;
    public static final int PUBLIC_SELECTIVE_COURSE = 1;
    public static final int MAJOR_SELECTIVE_COURSE = 2;
    private static final int COURSE_TYPE_NUM = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer type;

    @Column(length = 3)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        if (type <= COURSE_TYPE_NUM && type >= 0) {
            this.type = type;
        }
    }

    @ManyToOne
    @JoinColumn(name = "student_id")
    private UserEntity student;

    public UserEntity getStudent() {
        return student;
    }

    public void setStudent(UserEntity student) {
        this.student = student;
    }

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }
}
