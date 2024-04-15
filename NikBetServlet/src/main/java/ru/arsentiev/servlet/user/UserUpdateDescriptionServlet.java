package ru.arsentiev.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.UserConstFieldsDto;
import ru.arsentiev.dto.user.UserDto;
import ru.arsentiev.dto.user.UserUpdateDescriptionDto;
import ru.arsentiev.service.UserService;
import ru.arsentiev.service.entity.user.ReturnValueOnUpdateDescription;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.validator.entity.load.LoadError;
import ru.arsentiev.validator.entity.update.UpdatedUserFields;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERRORS;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_UPDATE_DESCRIPTION_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_DEFAULT_URL;
import static ru.arsentiev.utils.UrlPathGetter.USER_UPDATE_DESCRIPTION_URL;

@WebServlet(USER_UPDATE_DESCRIPTION_URL)
public class UserUpdateDescriptionServlet extends HttpServlet {
    private static UserService userService;
    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_UPDATE_DESCRIPTION_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDto user = (UserDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        String nickname = req.getParameter("nickname");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String patronymic = req.getParameter("patronymic");
        String email = req.getParameter("email");
        String phoneNumber = req.getParameter("phoneNumber");
        String birthDate = req.getParameter("birthDate");

        final var updatedUserFields = getUpdatedUserFields(nickname, user, phoneNumber,
                patronymic, email, birthDate, firstName, lastName);
        final var userUpdateDto = getUserUpdateDto(nickname, firstName, lastName, patronymic,
                email, phoneNumber, birthDate, user.idUser().toString());
        UserConstFieldsDto userConstFieldsDto = new UserConstFieldsDto(user.idUser(), user.role());
        ReturnValueOnUpdateDescription result = userService.updateDescriptionUser(userUpdateDto, updatedUserFields, userConstFieldsDto);
        if (result.errors().isEmpty()) {
            req.getSession().removeAttribute(NAME_ATTRIBUTE_USER);
            req.getSession().setAttribute(NAME_ATTRIBUTE_USER, result.userDto());
            resp.sendRedirect(USER_DEFAULT_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERRORS, result.errors());
            doGet(req, resp);
        }
    }

    private static UserUpdateDescriptionDto getUserUpdateDto(String nickname, String firstName, String lastName,
                                                             String patronymic, String email, String phoneNumber,
                                                             String birthDate, String idUser) {
        return UserUpdateDescriptionDto.builder()
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

    private static UpdatedUserFields getUpdatedUserFields(String nickname, UserDto user, String phoneNumber, String patronymic, String email, String birthDate, String firstName, String lastName) {
        return UpdatedUserFields.builder()
                .isNewNickName(!Objects.equals(nickname, user.nickname()))
                .isNewPhoneNumber(!Objects.equals(phoneNumber, user.phoneNumber()))
                .isNewPatronymic(!Objects.equals(patronymic, user.patronymic()))
                .isNewEmail(!Objects.equals(email, user.email()))
                .isNewBirthDate(!Objects.equals(birthDate, user.birthDate().toString()))
                .isNewFirstName(!Objects.equals(firstName, user.firstName()) )
                .isNewLastName(!Objects.equals(lastName, user.lastName()))
                .build();
    }
}
