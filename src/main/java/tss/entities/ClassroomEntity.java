package tss.entities;

import tss.models.TimeSlotTypeEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author reeve
 */
@Entity
@Table(name = "classroom")
public class ClassroomEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 32, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id")
    private BuildingEntity building;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlotEntity> timeSlots = new ArrayList<>();

    @Transient
    private Map<TimeSlotTypeEnum, TimeSlotEntity> timeSlotDirectory;

    public ClassroomEntity() {
    }

    public ClassroomEntity(String name, Integer capacity, BuildingEntity building) {
        this.name = name;
        this.capacity = capacity;
        this.building = building;
    }


    // Getter and setter.

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public void setBuilding(BuildingEntity building) {
        this.building = building;
    }

    public List<TimeSlotEntity> getTimeSlots() {
        return timeSlots;
    }

    public Map<TimeSlotTypeEnum, TimeSlotEntity> getTimeSlotDirectory() {
        if (timeSlotDirectory == null) {
            timeSlotDirectory = new HashMap<>(timeSlots.size());
            for (TimeSlotEntity timeSlotEntity : timeSlots) {
                timeSlotDirectory.put(timeSlotEntity.getType(), timeSlotEntity);
            }
        }
        return timeSlotDirectory;
    }

    // Utility methods.

    public void addTimeSlot(TimeSlotEntity timeSlotEntity) {
        timeSlotEntity.setClassroom(this);
        timeSlots.add(timeSlotEntity);
        getTimeSlotDirectory().put(timeSlotEntity.getType(), timeSlotEntity);
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
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        } else if (id != null) {
            return (id.equals(((ClassroomEntity) obj).id));
        } else {
            return super.equals(obj);
        }
    }
}
