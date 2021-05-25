package platform.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import platform.repository.CodeRepository;
import platform.model.Code;
import platform.exception.CodeNotFoundException;
import platform.utils.FormatDataTime;

import java.time.LocalTime;
import java.util.*;

@Service
public class CodeService {

    final Logger log = org.slf4j.LoggerFactory.getLogger(CodeService.class);

    @Autowired
    CodeRepository repository;

    public String addCode(Code code) {
        log.debug("Request to addCode: {}", code);
        Code newCode = new Code();
        newCode.setCode(code.getCode());
        newCode.setLocalDateTime();
        newCode.setDate(FormatDataTime.getFormatterDateTime());
        newCode.setTime(code.getTime());
        newCode.setViews(code.getViews());
        newCode.setTimeLimit(code.getTime() > 0);
        newCode.setViewsLimit(code.getViews() > 0);
        newCode.setUuid(UUID.randomUUID());
        UUID result = newCode.getUuid();
        log.debug("New snippet to repository: {}", newCode);
        repository.save(newCode);;
        return "{ \"id\" : \"" + result + "\" }";
    }

    public Code[] getLatestCode() {
        log.debug("Request to getLatestCode");
        Arrays.toString(repository.findAllByTimeAndViewsOrderByDate().toArray(new Code[0]));
        return repository.findAllByTimeAndViewsOrderByDate().toArray(new Code[0]);
    }

    public Code getCodeFromRepository(UUID uuid) {
        log.debug("Request to getCodeFromRepository: {}", uuid);
        if (repository.findById(uuid).isPresent()) {
            log.debug("Snippet from repository: {}", repository.findById(uuid).get().toString());
            return repository.findById(uuid).get();
        } else {
            throw new CodeNotFoundException();
        }
    }

    void deleteCodeFromRepository(Code code) {
        log.debug("Request to deleteCodeFromRepository: {}", code);
        repository.delete(code);
    }

    public void updateTimeById(UUID uuid) {
        log.debug("Request to updateTimeById: {}", uuid);
        Code codeToUpdate = getCodeFromRepository(uuid);
        int time = codeToUpdate.getTime();
        codeToUpdate.setTime(setTimeToSecretCode(codeToUpdate));
        log.debug("Snippet was updated, now time is {}", codeToUpdate.getTime());
        if (codeToUpdate.getTime() > 0) {
            repository.save(codeToUpdate);
        } else {
            repository.delete(codeToUpdate);
        }
    }

    public void updateViewsById(UUID uuid) {
        log.debug("Request to updateViewsById: {}", uuid);
        Code codeToUpdate = getCodeFromRepository(uuid);
        int views = codeToUpdate.getViews();
        views--;
        codeToUpdate.setViews(views);
        if (codeToUpdate.getViews() >= 0) {
            repository.save(codeToUpdate);
        } else {
            repository.delete(codeToUpdate);
        }
        log.debug("Snippet was updated, now views is {}", codeToUpdate.getViews());
    }

    boolean isViewsOver(Code code) {
        log.debug("Request to isViewsOver: {}", code);
        int viewsToWatch = code.getViews();
        log.debug("Check for views, views is {}", viewsToWatch);
        return viewsToWatch < 0;
    }

    boolean isTimeOver(Code code) {
        log.debug("Request to isTimeOver: {}", code);
        int timeToWatch = code.getTime();
        log.debug("Check time, time is {}", timeToWatch);
        LocalTime timeOfCreation = getTimeOfCreation(code);
        log.debug("TimeOfCreation is {}", timeOfCreation);
        LocalTime localTime = LocalTime.now();
        log.debug("LocalTime is {}", localTime);
        int differenceOfTime = localTime.toSecondOfDay() - timeOfCreation.toSecondOfDay();;
        log.debug("Difference of time is {}", differenceOfTime);
        return differenceOfTime > timeToWatch;
    }

    LocalTime getTimeOfCreation(Code code) {
        log.debug("Request to getTimeOfCreation: {}", code);
        return code.getLocalDateTime().toLocalTime();
    }

    Integer setTimeToSecretCode(Code code) {
        log.debug("Request to setTimeToSecretCode: {}", code);
        int result = 0;
        int timeToWatch = code.getTime();
        LocalTime timeOfCreation = getTimeOfCreation(code);
        LocalTime localTime = LocalTime.now();
        int difference = localTime.toSecondOfDay() - timeOfCreation.toSecondOfDay();
        if (difference > 0) {
            result = timeToWatch - difference;
        }
        return result;
    }

}