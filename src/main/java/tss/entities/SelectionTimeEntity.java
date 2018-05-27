package tss.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "selection_time")
public class SelectionTimeEntity {
    private int stid;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean regiPerm; // registrationPermissive
    private boolean dismPerm; // dismissPermissive
    private boolean lowCrePerm; // lowCreditRegistrationPermissive
    private boolean compPerm; // complementPermissive

    @Id
    @Column(name = "stid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return stid;
    }
    public void setId(int stid) {
        this.stid = stid;
    }

    @Column(name = "start_time", nullable = false)
    public Timestamp getStartTime() {
        return startTime;
    }
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }


    @Column(name = "end_time", nullable = false)
    public Timestamp getEndTime() {
        return endTime;
    }
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }


    @Column(name = "regi_perm", nullable = false)
    public boolean getRegiPerm() {
        return regiPerm;
    }
    public void setRegiPerm(boolean regiPerm) {
        this.regiPerm = regiPerm;
    }


    @Column(name = "dism_perm", nullable = false)
    public boolean getDismPerm() {
        return dismPerm;
    }
    public void setDismPerm(boolean dismPerm) {
        this.dismPerm = dismPerm;
    }

    @Column(name = "low_cre_perm", nullable = false)
    public boolean getLowCrePerm() {
        return lowCrePerm;
    }
    public void setLowCrePerm(boolean lowCrePerm) {
        this.lowCrePerm = lowCrePerm;
    }

    @Column(name = "comp_perm", nullable = false)
    public boolean getCompPerm() {
        return compPerm;
    }
    public void setCompPerm(boolean compPerm) {
        this.compPerm = compPerm;
    }

}