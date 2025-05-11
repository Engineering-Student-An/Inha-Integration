package an.inhaintegration.rentalee.service;

import an.inhaintegration.icross.domain.Task;
import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.icross.domain.UnivInfoTask;
import an.inhaintegration.icross.exception.UnivInfoNotFoundException;
import an.inhaintegration.icross.repository.TaskRepository;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import an.inhaintegration.icross.repository.UnivInfoTaskRepository;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.exception.StudentNotFoundException;
import an.inhaintegration.rentalee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnivInfoService {

    private final UnivInfoRepository univInfoRepository;
    private final UnivInfoTaskRepository univInfoTaskRepository;
    private final StudentRepository studentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public void save(Long studentId, String password) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElse(null);

        if(univInfo == null) {
            UnivInfo newUnivInfo = UnivInfo.builder()
                    .password(password)
                    .build();
            univInfo.setStudent(loginStudent);

            univInfoRepository.save(univInfo);
        } else {
            univInfo.setPassword(password);
        }
    }

    @Transactional
    public void saveUnivInfoTask(Long studentId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        List<Task> tasks = taskRepository.findTasksBySubjectIdIn(univInfo.getSubjectList());

        for (Task task : tasks) {
            // UnivInfoTask 가 존재하지 않는 경우 생성
            if(!univInfoTaskRepository.existsByUnivInfoIdAndTaskId(univInfo.getId(), task.getId())) {
                UnivInfoTask univInfoTask = UnivInfoTask.builder()
                        .completed(false)
                        .build();
                univInfoTask.setUnivInfoAndTask(univInfo, task);

                univInfoTaskRepository.save(univInfoTask);
            }
        }
    }
}
