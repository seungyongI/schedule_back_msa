package com.example.calendarService.service;

import com.example.calendarService.constant.Provider;
import com.example.calendarService.constant.Theme;
import com.example.calendarService.dto.request.UserRequestInsertDto;
import com.example.calendarService.dto.request.UserRequestUpdateDto;
import com.example.calendarService.entity.Calendars;
import com.example.calendarService.entity.ProfileImage;
import com.example.calendarService.entity.User;
import com.example.calendarService.exception.commonException.CommonErrorCode;
import com.example.calendarService.exception.commonException.error.BizException;
import com.example.calendarService.exception.commonException.error.MyInternalServerError;
import com.example.calendarService.exception.loginException.DuplicateEmailException;
import com.example.calendarService.exception.loginException.LoginErrorCode;
import com.example.calendarService.exception.loginException.UserPKException;
import com.example.calendarService.exception.userException.UserErrorCode;
import com.example.calendarService.exception.userException.UserNotFoundException;
import com.example.calendarService.exception.userException.ValidationError;
import com.example.calendarService.repository.CalendarRepository;
import com.example.calendarService.repository.ProfileImageRepository;
import com.example.calendarService.repository.UserRepository;
import com.example.calendarService.security.providers.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;
    private final ProfileImageRepository profileImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final EntityManager entityManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final Validator validator;

    // 회원가입
    @Override
    @Transactional
    public User createUser(@Valid UserRequestInsertDto userRequestInsertDto) {

        Set<ConstraintViolation<UserRequestInsertDto>> violations = validator.validate(userRequestInsertDto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ValidationError(UserErrorCode.VALIDATION_ERROR);
        }

        try {
            Calendars calendars = Calendars.builder().theme(Theme.LIGHT).build();
            log.info("calendar = {}", calendarRepository.save(calendars));

            // 기존 회원가입 로직
            User user = User.builder()
                    .email(userRequestInsertDto.getEmail())
                    .password(passwordEncoder.encode(userRequestInsertDto.getPassword()))
                    .userName(userRequestInsertDto.getUserName())
                    .provider(Provider.LOCAL)
                    .calendars(calendars)
                    .build();

            return userRepository.save(user);

        } catch (DuplicateKeyException e) {
            throw new DuplicateEmailException(LoginErrorCode.DUPLICATE_EMAIL);
        } catch (Exception e) {
            throw new MyInternalServerError(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // 회원 찾기
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BizException(CommonErrorCode.NOT_FOUND));
    }


    // 닉네임 수정
    @Override
    @Transactional
    public void updateUserName(@Valid UserRequestUpdateDto userRequestUpdateDto) {
        try {
            User updateUser = userRepository.findById(userRequestUpdateDto.getIdx())
                    .orElseThrow(() -> new BizException(CommonErrorCode.NOT_FOUND));

            if (userRequestUpdateDto.getUserName() != null) {
                updateUser.setUserName(userRequestUpdateDto.getUserName());
            }

            userRepository.save(updateUser);

        } catch (Exception e) {
            throw new RuntimeException("닉네임 업데이트 중 오류 발생", e);
        }
    }


    // 프로필 사진 수정
    @Override
    @Transactional
    public void updateProfileImage(Long idx, MultipartFile imageFile) {

            User updateUser = userRepository.findById(idx)
                    .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
            // 기존 이미지 삭제 로직 추가
        try {
            ProfileImage oldProfileImage = updateUser.getProfileImage();
            if (oldProfileImage != null) {
                updateUser.setProfileImage(null);
                profileImageRepository.delete(oldProfileImage);
                entityManager.flush();
            }

            if (imageFile != null) {
                ProfileImage profileImage = imageService.saveProfileImage(imageFile, updateUser);
                profileImage.setUser(updateUser);
                updateUser.setProfileImage(profileImage);
            }
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 업데이트 중 오류 발생", e);
        }
    }


    @Transactional
    @Override
    public void deleteUser(String email, String authToken) {
        String userIdFromToken = jwtTokenProvider.getUserPk(authToken);

        if (!userIdFromToken.equals(email)) {
            throw new UserPKException(LoginErrorCode.USER_PK);
        }

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BizException(CommonErrorCode.NOT_FOUND)
        );

        userRepository.deleteById(user.getIdx());
    }
}