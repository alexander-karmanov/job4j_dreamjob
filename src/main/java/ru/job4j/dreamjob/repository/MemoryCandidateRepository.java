package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import net.jcip.annotations.ThreadSafe;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Иван Иванов", "Text", LocalDateTime.now(), 1));
        save(new Candidate(0, "Семен Петров", "Text", LocalDateTime.now(), 2));
        save(new Candidate(0, "Андрей Семенов", "Text", LocalDateTime.now(), 3));
        save(new Candidate(0, "Анатолий Новиков", "Text", LocalDateTime.now(), 1));
        save(new Candidate(0, "Анастасия Кузнецова", "Text", LocalDateTime.now(), 1));
        save(new Candidate(0, "Юлия Сидорова", "Text", LocalDateTime.now(), 2));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> {
            return new Candidate(
                    oldCandidate.getId(), candidate.getName(), candidate.getDescription(),
                    candidate.getCreationDate(), candidate.getCityId()
            );
        }) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
