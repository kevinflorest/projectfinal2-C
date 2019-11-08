package com.sistema.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.sistema.app.models.Course;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseDAO extends ReactiveMongoRepository<Course, String> {

	Mono<Course> findByCodCourse(String codCourse);
	
	Flux<Course> findCourseByCodTeacher(String codTeacher);
	
}
