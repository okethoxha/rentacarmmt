package io.appwebgo.rentacar.services;

import io.appwebgo.rentacar.Domain.Backlog;
import io.appwebgo.rentacar.Domain.Project;
import io.appwebgo.rentacar.Domain.ProjectTask;
import io.appwebgo.rentacar.exceptions.ProjectNotFoundException;
import io.appwebgo.rentacar.repositories.BacklogRepository;
import io.appwebgo.rentacar.repositories.ProjectRepository;
import io.appwebgo.rentacar.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){

        try {
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

            projectTask.setBacklog(backlog);

            Integer BacklogSequence = backlog.getPTSequence();
            BacklogSequence++;

            backlog.setPTSequence(BacklogSequence);

            projectTask.setProjectSequence(projectIdentifier+"-"+BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            if(projectTask.getPriority() == null){
                projectTask.setPriority(3);
            }

            if(projectTask.getStatus() == "" || projectTask.getStatus() == null){
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
        }catch (Exception e){
            throw new ProjectNotFoundException("Project not found!");
        }



    }

   public Iterable<ProjectTask>findBacklogById(String id){

       Project project = projectRepository.findByProjectIdentifier(id);

       if(project == null){
           throw new ProjectNotFoundException("Project with ID "+id+" does not exist!");
       }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id){

        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);

        if(backlog == null){
            throw new ProjectNotFoundException("Project with ID "+backlog_id+" does not exist!");
        }

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if(projectTask == null){
            throw new ProjectNotFoundException("Project task with ID "+pt_id+" does not exist!");
        }

        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project task "+pt_id +"does not exists in project "+backlog_id);
        }

        return projectTask;
    }
}
