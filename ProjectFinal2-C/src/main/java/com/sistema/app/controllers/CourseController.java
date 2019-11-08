package com.sistema.app.controllers;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import com.sistema.app.models.Course;
import com.sistema.app.service.CourseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/course")
public class CourseController {

	@Autowired
	private CourseService service;

	@GetMapping
	public Mono<ResponseEntity<Flux<Course>>> findAllCareer(){
		return Mono.justOrEmpty(
				ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAllCourse())
				);
	}
	
	
	@GetMapping("select2/{codCareer}")
	public Mono<ResponseEntity<Course>> viewCourse(@PathVariable String codCourse){
		return service.findByCodCourse(codCourse).map(p-> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
			
	}
	
	@GetMapping("select/{id}")
	public Mono<ResponseEntity<Course>> viewCourseId(@PathVariable String id){
		return service.findByIdCourse(id).map(p-> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
			
	}
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> saveCourse(@Valid @RequestBody Mono<Course> monoCourse){
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		return monoCourse.flatMap(course -> {
			return service.saveCourse(course).map(c-> {
				response.put("Course", c);
				response.put("mensaje", "Curso registrado con éxito");
				response.put("timestamp", new Date());
				return ResponseEntity
					.created(URI.create("/api/course/".concat(c.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(response);
				});
			
		}).onErrorResume(r -> {
			return Mono.just(r).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo "+fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						response.put("errors", list);
						response.put("timestamp", new Date());
						response.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(response));
					});		
		});
	}
	
	@PostMapping("all/")
	public Flux<Course> saveCourses(@Valid @RequestBody List<Course> course){
			return service.saveCourses(course);
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Course>> updateCourse(@RequestBody Course course, @PathVariable String id)
	{
		return service.findByIdCourse(id)
				.flatMap(c -> {
					c.setCodCourse(course.getCodCourse());
					c.setNameCourse(course.getNameCourse());
					c.setStatusCourse(course.getStatusCourse());
					c.setMinCapacityCourse(course.getMaxCapacityCourse());
					c.setCodCareerLine(course.getCodCareerLine());
					c.setCodTeacher(course.getCodTeacher());
					c.setTimeDuration(course.getTimeDuration());
					return service.saveCourse(c);
				}).map(s -> ResponseEntity.created(URI.create("/api/career/".concat(s.getId())))
				  .contentType(MediaType.APPLICATION_JSON)
				  .body(s))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}	
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteCourse(@PathVariable String id)
	{
		return service.findByIdCourse(id).flatMap(c -> {
			return service.deleteCourse(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));		
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));
	}
}
