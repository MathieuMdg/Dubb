package com.lonelys.Dubb.config;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.Movie;
import com.lonelys.Dubb.repository.ClipRepository;
import com.lonelys.Dubb.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ClipRepository clipRepository;

    public DataInitializer(MovieRepository movieRepository, ClipRepository clipRepository) {
        this.movieRepository = movieRepository;
        this.clipRepository = clipRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() == 0) { // Checking if the database isn't already populated

            // new movies
            Movie shrek = new Movie("Shrek 2", 2004, "Andrew Adamson");
            Movie nah = new Movie("Spider-Man: Across the Spider-Verse", 2023, "Justin K. Thompson");
            Movie dead = new Movie("Deadpool", 2016, "Tim Miller");
            Movie aven = new Movie("Avengers: Endgame", 2019, "Russo Brothers");
            Movie mega = new Movie("Megamind", 2010, "Tom McGrath");
            Movie fldsmdfr = new Movie("Cloudy with a Chance of Meatballs", 2009, "Phil Lord");
            Movie walle = new Movie("Wall-E", 2008, "Andrew Stanton");
            Movie sw = new Movie("Star Wars: Episode III – Revenge of the Sith", 2005, "George Lucas");


            // saving movies
            movieRepository.save(shrek);
            movieRepository.save(nah);
            movieRepository.save(dead);
            movieRepository.save(aven);
            movieRepository.save(mega);
            movieRepository.save(fldsmdfr);
            movieRepository.save(walle);
            movieRepository.save(sw);

            // new clips
            Clip clip1 = new Clip("shrek ahh", 60.0, shrek);
            Clip clip2 = new Clip("The original anomaly", 60.0, nah);
            Clip clip3 = new Clip("Intro scene", 60.0, dead);
            Clip clip4 = new Clip("Assemble...", 60.0, aven);
            Clip clip5 = new Clip("megamind ahh", 60.0, mega);
            Clip clip6 = new Clip("The FLDSMDFR!", 60.0, fldsmdfr);
            Clip clip7 = new Clip("I want to live.", 60.0, walle);
            Clip clip8 = new Clip("Sexy back", 60.0, sw);

            // saving the clips
            clipRepository.save(clip1);
            clipRepository.save(clip2);
            clipRepository.save(clip3);
            clipRepository.save(clip4);
            clipRepository.save(clip5);
            clipRepository.save(clip6);
            clipRepository.save(clip7);
            clipRepository.save(clip8);
        }
    }
}