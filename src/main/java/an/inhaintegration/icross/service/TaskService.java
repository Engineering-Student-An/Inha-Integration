package an.inhaintegration.icross.service;

import an.inhaintegration.icross.domain.TaskType;
import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.icross.domain.UnivInfoTask;
import an.inhaintegration.icross.dto.TaskResponseDto;
import an.inhaintegration.icross.exception.TaskNotFoundException;
import an.inhaintegration.icross.exception.UnivInfoNotFoundException;
import an.inhaintegration.icross.repository.TaskRepository;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import an.inhaintegration.icross.repository.UnivInfoTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UnivInfoTaskRepository univInfoTaskRepository;
    private final UnivInfoRepository univInfoRepository;

    public List<TaskResponseDto> findInCompleteTasksByStudentIdAndIsVideo(Long studentId, boolean isVideo) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        // 검색할 타입 정의
        List<TaskType> taskTypes = isVideo ? List.of(TaskType.VIDEO) : List.of(TaskType.ASSIGNMENT, TaskType.QUIZ);
        List<UnivInfoTask> result = univInfoTaskRepository.findIncompleteTasksByUnivInfoIdAndTaskTypes(univInfo.getId(), taskTypes);

        return result.stream()
                .map(UnivInfoTask::mapUnivInfoToTaskResponseDto)
                .toList();
    }

    @Transactional
    public void completeTask(Long studentId, Long webId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        UnivInfoTask univInfoTask = univInfoTaskRepository.findUnivInfoTaskByUnivInfoIdAndTaskIdAndCompletedIs(univInfo.getId(), webId, false).orElseThrow(TaskNotFoundException::new);

        // 완료 처리
        univInfoTask.setCompleted();
    }
}
