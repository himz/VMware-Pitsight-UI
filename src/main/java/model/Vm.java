package model;

import java.util.ArrayList;
import java.util.List;
public class Vm
{
    private String name;

    private String policyRpo;

    private List<Snapshots> snapshots;

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPolicyRpo(String policyRpo){
        this.policyRpo = policyRpo;
    }
    public String getPolicyRpo(){
        return this.policyRpo;
    }
    public void setSnapshots(List<Snapshots> snapshots){
        this.snapshots = snapshots;
    }
    public List<Snapshots> getSnapshots(){
        return this.snapshots;
    }
}
