package com.lonelys.Dubb;

import com.lonelys.Dubb.entity.*;
import com.lonelys.Dubb.repository.AttemptRepository;
import com.lonelys.Dubb.repository.ClipRepository;
import com.lonelys.Dubb.repository.MovieRepository;
import com.lonelys.Dubb.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class DubbApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(DubbApplication.class, args);
	}


}