package com.tcg.weatherinfo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.weatherinfo.entity.User;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Disable embedded DB replacement
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	// Use a fixed time to avoid issues with time-based fields
	private static final LocalDateTime FIXED_TIME = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@Autowired
    private EntityManager entityManager;
	
	@Test
	void testFindByUsername() {
		User user = User.builder()
				.username("testuser")
				.password("password")
				.active(true)
				.createdAt(FIXED_TIME)
				.build();

		userRepository.save(user);

		Optional<User> foundUser = userRepository.findByUsername("testuser");
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
	}

	@Test
	void testExistsByUsername() {
		User user = User.builder()
				.username("testuser")
				.password("password")
				.active(true)
				.createdAt(FIXED_TIME)
				.build();
		userRepository.save(user);

		boolean exists = userRepository.existsByUsername("testuser");
		assertThat(exists).isTrue();
	}

	@Test
	@Transactional
	void testUpdateUserActiveStatus() {
	    

	    User user = User.builder()
	            .username("testuser1")
	            .password("password")
	            .active(true)
	            .createdAt(FIXED_TIME)
	            .build();

	    // Save the user and let JPA generate the id
	    user = userRepository.save(user);

	    // Update active status
	    userRepository.updateUserActiveStatus(user.getId(), false);

	    // Flush the changes and clear the persistence context
	    userRepository.flush();
	    entityManager.refresh(user);


	    // Retrieve the updated user
	    Optional<User> updatedUser = userRepository.findById(user.getId());
	    System.out.println("UPDATED User Details ==> " + updatedUser.toString());

	    // Assertions
	    assertThat(updatedUser).isPresent();
	    assertThat(updatedUser.get().isActive()).isFalse();
	}
}
