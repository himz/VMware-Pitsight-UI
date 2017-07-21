package model;

import java.util.ArrayList;
import java.util.List;

public class Snapshots
{
    private String id;

    private String timestamp;

    private List<String> processList;

    private List<String> loggedUsersList;

    private List<String> topTenFilesList;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
    public String getTimestamp(){
        return this.timestamp;
    }
    public void setProcessList(List<String> processList){
        this.processList = processList;
    }
    public List<String> getProcessList(){
        return this.processList;
    }
    public void setLoggedUsersList(List<String> loggedUsersList){
        this.loggedUsersList = loggedUsersList;
    }
    public List<String> getLoggedUsersList(){
        return this.loggedUsersList;
    }
    public void setTopTenFilesList(List<String> topTenFilesList){
        this.topTenFilesList = topTenFilesList;
    }
    public List<String> getTopTenFilesList(){
        return this.topTenFilesList;
    }
}