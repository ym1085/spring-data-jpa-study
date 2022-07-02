package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {

    @Value("#{target.userName + ' ' + target.age}")
    String getUserName();

}
