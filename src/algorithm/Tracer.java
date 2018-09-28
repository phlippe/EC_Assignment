package algorithm;

import island.DistributedEvolutionaryCycle;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tracer {

    private boolean isActive;
    private String name;
    private String file_basepath;
    private String date_string;
    private ArrayList<TraceObject> traceFiles;
    private Tracer parentTracer;


    public Tracer(boolean active, String name){
        isActive = active;
        this.name = name;
        if(this.name.length() == 0)
            this.name = "trace";
        traceFiles = new ArrayList<>();
    }

    public void initialize(){
        traceFiles = new ArrayList<>();
        prepareFolder();
    }

    public void setParentTracer(Tracer parent){
        parentTracer = parent;
    }

    private void prepareFolder(){
        if(isActive){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();
            date_string = dateFormat.format(date);
            file_basepath = "trace/";
            if(parentTracer != null)
                file_basepath += parentTracer.getName() + "/";
            file_basepath += name + "_" + date_string + "/";
            new File(file_basepath).mkdirs();
        }
    }

    public void addTraceFile(String tag){
        if(isActive)
            traceFiles.add(new TraceObject(tag));
    }

    public void addTraceContent(String tag, String content){
        if(isActive)
            for(TraceObject to: traceFiles)
                if(to.tag.equals(tag)) {
                    to.addContent(content);
                    break;
                }
    }

    public void addTraceContent(String tag, double content){
        addTraceContent(tag, "" + content);
    }

    public void addTraceContent(String tag, int content){
        addTraceContent(tag, "" + content);
    }

    public void addTraceContent(String tag, double[] content){
        String s = "";
        for(int i=0;i<content.length;i++){
            if(i > 0)
                s += ", ";
            s += content[i];
        }
        addTraceContent(tag, s);
    }

    public void addTraceContent(String tag, int[] content) {
        String s = "";
        for (int i = 0; i < content.length; i++) {
            if (i > 0)
                s += ", ";
            s += content[i];
        }
        addTraceContent(tag, s);
    }

    public boolean isActive(){
        return isActive;
    }

    public String getName(){
        return name + "_" + date_string;
    }


    public void writeOut(){
        if(isActive){
            for(TraceObject to: traceFiles){
                String filename = file_basepath + to.tag + ".txt";
                try (PrintWriter out = new PrintWriter(filename)) {
                    out.print(to.getContent());
                }
                catch (Exception e){
                    System.out.println("Could not write results to file. Error message:");
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    class TraceObject{

        private String tag;
        private String content;

        public TraceObject(String tag){
            this.tag = tag;
            content = "";
        }

        public void addContent(String s){
            content += s + "\n";
        }

        public String getTag(){
            return tag;
        }

        public String getContent(){
            return content;
        }
    }

}
