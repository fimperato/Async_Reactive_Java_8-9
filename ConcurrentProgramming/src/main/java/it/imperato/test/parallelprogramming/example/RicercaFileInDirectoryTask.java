package it.imperato.test.parallelprogramming.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RicercaFileInDirectoryTask extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LogManager.getLogger(RicercaFileInDirectoryTask.class);
    
	private static final String PATH_DA_RICERCARE = "/home/francesco/Immagini";
	
	private static long count = 0;

    private final File file;

    public RicercaFileInDirectoryTask (File file) {
        this.file = file;
    }

    @Override
    protected void compute() {
        List<RicercaFileInDirectoryTask> tasks = new ArrayList<>();
        
        File[] files = file.listFiles();
        
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                	RicercaFileInDirectoryTask newTask = new RicercaFileInDirectoryTask(f);
                    tasks.add(newTask);
                    newTask.fork();
                } else if (f.getName().endsWith(".png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".xcf")) {
                	log.info(f.getAbsolutePath());
                    count++;
                }
            }
        }
        
        if (tasks.size() > 0) {
            for (RicercaFileInDirectoryTask task : tasks) {
                task.join();
            }
        }
        
    }

    public static void main (String[] args) {
        long time = System.currentTimeMillis();
        RicercaFileInDirectoryTask fileSearchTask = new RicercaFileInDirectoryTask(new File(PATH_DA_RICERCARE));
        
        // Chiamo i task in parallelo:
        ForkJoinPool.commonPool().invoke(fileSearchTask);
        
        log.info("Time elaborazione parallela : "+(System.currentTimeMillis()-time));
        log.info("Trovati n. file : "+RicercaFileInDirectoryTask.count);
    }
    
}