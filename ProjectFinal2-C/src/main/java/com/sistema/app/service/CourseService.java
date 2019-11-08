package com.sistema.app.service;


import java.util.List;
import com.sistema.app.models.Course;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseService {
	
	Flux<Course> findAllCourse();
	
	Mono<Course> findByIdCourse(String id);
	
	Mono<Course> findByCodCourse(String codCourse);
	
	Mono<Course> saveCourse(Course course);
	
	Flux<Course> saveCourses(List<Course> course);
		
	Mono<Void> deleteCourse(Course course);
	
	Mono<Long> findCourseByCodTeacher(String codCourse);
	
}
