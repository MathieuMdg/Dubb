package com.lonelys.Dubb;

import com.lonelys.Dubb.entity.*;
import com.lonelys.Dubb.repository.AttemptRepository;
import com.lonelys.Dubb.repository.ClipRepository;
import com.lonelys.Dubb.repository.MovieRepository;
import com.lonelys.Dubb.service.SegmentRecordingService;
import com.lonelys.Dubb.service.SegmentService;
import com.lonelys.Dubb.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


@SpringBootApplication
public class DubbApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(DubbApplication.class, args);
	}

	@Bean
	CommandLineRunner testSegmentRecordingService(
			UserService userService,
			MovieRepository movieRepository,
			ClipRepository clipRepository,
			SegmentService segmentService,
			AttemptRepository attemptRepository,
			SegmentRecordingService segmentRecordingService) {

		return args -> {

			System.out.println("\n===== TEST SegmentRecordingService =====\n");

			// --- Mise en place des données de test ---

			User user = userService.createUser("testuser", "testuser@mail.com");

			Movie movie = new Movie("Test Movie", 2020, "Test Director");
			movie = movieRepository.save(movie);

			Clip clip = new Clip("Test Clip", 10.0, movie);
			clip = clipRepository.save(clip);

			Segment segmentDubbable = segmentService.createSegment(clip, 0.0, 3.0, 1, true);
			Segment segmentNonDubbable = segmentService.createSegment(clip, 3.0, 6.0, 2, false);

			Attempt attemptEnCours = new Attempt();
			attemptEnCours.setUser(user);
			attemptEnCours.setClip(clip);
			attemptEnCours.setStatus("IN_PROGRESS");
			attemptEnCours.setStartedAt(LocalDateTime.now());
			attemptEnCours = attemptRepository.save(attemptEnCours);

			Attempt attemptTermine = new Attempt();
			attemptTermine.setUser(user);
			attemptTermine.setClip(clip);
			attemptTermine.setStatus("COMPLETED");
			attemptTermine.setStartedAt(LocalDateTime.now());
			attemptTermine = attemptRepository.save(attemptTermine);

			MockMultipartFile audioTest = new MockMultipartFile(
					"audio", "test.wav", "audio/wav", "contenu audio simulé".getBytes(StandardCharsets.UTF_8)
			);

			// --- Cas 1 : enregistrement valide (segment dubbable, attempt en cours) ---
			try {
				SegmentRecording recording = segmentRecordingService.recordSegment(
						attemptEnCours.getAttemptID(), segmentDubbable.getSegmentID(), audioTest);
				System.out.println("✅ Cas 1 réussi : " + recording);
			} catch (Exception e) {
				System.out.println("❌ Cas 1 : échec inattendu : " + e.getMessage());
			}

			// --- Cas 2 : ré-enregistrement sur le même segment (doit écraser l'ancien) ---
			try {
				SegmentRecording recording = segmentRecordingService.recordSegment(
						attemptEnCours.getAttemptID(), segmentDubbable.getSegmentID(), audioTest);
				System.out.println("✅ Cas 2 réussi (écrasement) : " + recording);
			} catch (Exception e) {
				System.out.println("❌ Cas 2 : échec inattendu : " + e.getMessage());
			}

			// --- Cas 3 : tentative sur un segment NON dubbable (doit échouer) ---
			try {
				segmentRecordingService.recordSegment(
						attemptEnCours.getAttemptID(), segmentNonDubbable.getSegmentID(), audioTest);
				System.out.println("❌ Cas 3 : aurait dû échouer (segment non dubbable)");
			} catch (Exception e) {
				System.out.println("✅ Cas 3 : erreur attendue : " + e.getMessage());
			}

			// --- Cas 4 : tentative sur un Attempt déjà terminé (doit échouer) ---
			try {
				segmentRecordingService.recordSegment(
						attemptTermine.getAttemptID(), segmentDubbable.getSegmentID(), audioTest);
				System.out.println("❌ Cas 4 : aurait dû échouer (attempt non IN_PROGRESS)");
			} catch (Exception e) {
				System.out.println("✅ Cas 4 : erreur attendue : " + e.getMessage());
			}

			// --- Cas 5 : Attempt introuvable (doit échouer) ---
			try {
				segmentRecordingService.recordSegment(999999L, segmentDubbable.getSegmentID(), audioTest);
				System.out.println("❌ Cas 5 : aurait dû échouer (attempt introuvable)");
			} catch (Exception e) {
				System.out.println("✅ Cas 5 : erreur attendue : " + e.getMessage());
			}

			// --- Cas 6 : Segment introuvable (doit échouer) ---
			try {
				segmentRecordingService.recordSegment(attemptEnCours.getAttemptID(), 999999L, audioTest);
				System.out.println("❌ Cas 6 : aurait dû échouer (segment introuvable)");
			} catch (Exception e) {
				System.out.println("✅ Cas 6 : erreur attendue : " + e.getMessage());
			}

			// --- Vérification finale : un seul enregistrement doit exister pour attemptEnCours ---
			List<SegmentRecording> recordings = segmentRecordingService.getAllRecordsByAttempt(attemptEnCours.getAttemptID());
			System.out.println("\nNombre d'enregistrements pour l'attempt en cours : " + recordings.size() + " (attendu : 1, à cause de l'écrasement au cas 2)");
		};
	}
}