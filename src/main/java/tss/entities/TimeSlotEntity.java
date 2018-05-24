package tss.entities;

import tss.models.TimeSlotTypeEnum;

import javax.persistence.*;

/**
 * @author reeve
 */
@Entity
@Table(name = "time_slot")
public class TimeSlotEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TimeSlotTypeEnum type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id")
    private ClassroomEntity classroom;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity clazz;

    public TimeSlotEntity() {
    }

    public TimeSlotEntity(TimeSlotTypeEnum type, ClassroomEntity classroom, ClassEntity clazz) {
        this.type = type;
        this.classroom = classroom;
        this.clazz = clazz;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TimeSlotTypeEnum getType() {
        return type;
    }

    public void setType(TimeSlotTypeEnum type) {
        this.type = type;
    }

    public ClassroomEntity getClassroom() {
        return classroom;
    }

    public void setClassroom(ClassroomEntity classroom) {
        this.classroom = classroom;
    }

    public ClassEntity getClazz() {
        return clazz;
    }

    public void setClazz(ClassEntity clazz) {
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        } else {
            return id.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass()) || id == null) {
            return false;
        } else {
            return (id.equals(((TimeSlotEntity) obj).id));
        }
    }
}
