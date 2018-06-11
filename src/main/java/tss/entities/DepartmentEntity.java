package tss.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "department",
        indexes = {
                @Index(name = "dept_name_index", columnList = "dept_name", unique = true)
        }
)

public class DepartmentEntity {
    private short id;
    private String name;
    private Set<MajorEntity> majors = new HashSet<>();
    private Set<UserEntity> users = new HashSet<>();
    private Set<CourseEntity> courses = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    @Column(name = "dept_name", length = 20, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "department")
    public Set<MajorEntity> getMajors() {
        return majors;
    }

    public void setMajors(Set<MajorEntity> majors) {
        this.majors = majors;
    }

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "department")
    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "department")
    public Set<CourseEntity> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseEntity> courses) {
        this.courses = courses;
    }

    @Override
    public int hashCode() {
        if (name != null) {
            return name.hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        } else if (name != null) {
            return (name.equals(((DepartmentEntity) obj).name));
        } else {
            return super.equals(obj);
        }
    }
}
