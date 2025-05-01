package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.service.FileService;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import ru.job4j.dreamjob.dto.FileDto;
import java.util.Optional;

public class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    @BeforeEach
    void init() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenFileExistsThenReturnOkResponseWithContent() {
        var fileContent = new byte[]{1, 2, 3, 4, 5};
        var fileDto = new FileDto("testFile", fileContent);
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));

        ResponseEntity<?> response = fileController.getById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fileContent);
    }

    @Test
    void whenFileDoesNotExistThenReturnNotFoundResponse() {
        when(fileService.getFileById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = fileController.getById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
