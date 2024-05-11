package ru.arsentiev.servlet.user.update;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserConstFieldsControllerDto;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.dto.user.controller.UserUpdateDescriptionControllerDto;
import ru.arsentiev.dto.user.view.UserUpdateDescriptionViewDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.manager.ValidationManager;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.servicelayer.service.UserService;
import ru.arsentiev.servicelayer.validator.UpdateUserValidator;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.Objects;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERRORS;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_UPDATE_DESCRIPTION_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_DEFAULT_URL;
import static ru.arsentiev.utils.UrlPathGetter.USER_UPDATE_DESCRIPTION_URL;

@WebServlet(USER_UPDATE_DESCRIPTION_URL)
public class UserUpdateDescriptionServlet extends HttpServlet {
    private UserService userService;
    private UserMapper userMapper;
    private UpdateUserValidator updateUserValidator;

    @Override
    public void init() throws ServletException {
        userService = ServiceManager.getUserService();
        userMapper = MapperManager.getUserMapper();
        updateUserValidator = ValidationManager.getUpdateUserValidator();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_UPDATE_DESCRIPTION_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        final String nickname = req.getParameter("nickname");
        final String firstName = req.getParameter("firstName");
        final String lastName = req.getParameter("lastName");
        final String patronymic = req.getParameter("patronymic");
        final String email = req.getParameter("email");
        final String phoneNumber = req.getParameter("phoneNumber");
        final String birthDate = req.getParameter("birthDate");

        final UpdatedUserFields updatedUserFields = getUpdatedUserFields(nickname, user, phoneNumber,
                patronymic, email, birthDate, firstName, lastName);

        final UserUpdateDescriptionViewDto userUpdateDescriptionViewDto = getUserUpdateDto(nickname,
                firstName, lastName, patronymic, email, phoneNumber, birthDate, String.valueOf(user.idUser()));

        final LoadValidationResult result = updateUserValidator.isValidDescription(userUpdateDescriptionViewDto,
                updatedUserFields);

        if (result.getLoadErrors().isEmpty()) {

            final UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto =
                    userMapper.map(userUpdateDescriptionViewDto);

            final UserConstFieldsControllerDto userConstFieldsControllerDto = UserConstFieldsControllerDto.builder()
                    .idUser(user.idUser())
                    .role(user.role())
                    .build();

            final UserControllerDto userControllerDto = userService.updateDescriptionUser
                    (userUpdateDescriptionControllerDto, updatedUserFields, userConstFieldsControllerDto);

            req.getSession().removeAttribute(NAME_ATTRIBUTE_USER);
            req.getSession().setAttribute(NAME_ATTRIBUTE_USER, userControllerDto);
            resp.sendRedirect(USER_DEFAULT_URL);

        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERRORS, result.getLoadErrors());
            doGet(req, resp);
        }
    }

    private static UserUpdateDescriptionViewDto getUserUpdateDto(String nickname, String firstName, String lastName,
                                                                 String patronymic, String email, String phoneNumber,
                                                                 String birthDate, String idUser) {
        return UserUpdateDescriptionViewDto.builder()
                .idUser(idUser)
                .nickname(nickname)
                .firstName(firstName)
                .lastName(lastName)
                .patronymic(patronymic)
                .email(email)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .build();
    }

    private static UpdatedUserFields getUpdatedUserFields(String nickname, UserControllerDto user, String phoneNumber, String patronymic, String email, String birthDate, String firstName, String lastName) {
        return UpdatedUserFields.builder()
                .isNewNickName(!Objects.equals(nickname, user.nickname()))
                .isNewPhoneNumber(!Objects.equals(phoneNumber, user.phoneNumber()))
                .isNewPatronymic(!Objects.equals(patronymic, user.patronymic()))
                .isNewEmail(!Objects.equals(email, user.email()))
                .isNewBirthDate(!Objects.equals(birthDate, user.birthDate().toString()))
                .isNewFirstName(!Objects.equals(firstName, user.firstName()))
                .isNewLastName(!Objects.equals(lastName, user.lastName()))
                .build();
    }
}
