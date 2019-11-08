package com.sistema.app.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.sistema.app.dao.CourseDAO;
import com.sistema.app.models.CareerLine;
import com.sistema.app.exception.RequestException;
import com.sistema.app.exception.ResponseStatus;
import com.sistema.app.models.Course;
import com.sistema.app.models.Teacher;
import com.sistema.app.service.CourseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseServiceImpl implements CourseService{
	
	int sum = 0;
	int cant = 0;
	int sump = 0;
	String nomp = "";
	
	@Autowired
	private CourseDAO cdao;

	@Override
	public Flux<Course> findAllCourse() {
		return cdao.findAll();
	}

	@Override
	public Mono<Course> findByIdCourse(String id) {
		return cdao.findById(id);
	}

	@Override
	public Mono<Course> findByCodCourse(String codCourse) {
		return cdao.findByCodCourse(codCourse);
	}
	
	@Override
	public Mono<Long> findCourseByCodTeacher(String codCourse) {
		return cdao.findCourseByCodTeacher(codCourse).count();
	}

	@Override
	public Mono<Course> saveCourse(Course course) {
		
		Mono<Course> c = Mono.just(course);
		return c.flatMap(c2 -> {
			
			Mono<Long> c3 = cdao.findCourseByCodTeacher(c2.getCodTeacher()).count();
			
			return c3.flatMap(c4 -> {
			
					Mono<Boolean> c5 = WebClient
							.builder()
							.baseUrl("http://localhost:3032/api/teacher/")
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.build().get().uri("/select2/"+course.getCodTeacher()).retrieve()
							.bodyToMono(Teacher.class).hasElement();
							
					return c5.flatMap(c6 -> {
						if(c6)
						{
							return cdao.save(course);
						}
						else
						{
							throw new ResponseStatus("Profesor no existe");
						}
						
					});
					
			
				
			});
			
			
		});
		
	
	}

	
	@Override
	public Flux<Course> saveCourses(List<Course> course) {
		
		Flux<Course> cMono = Flux.fromIterable(course);
		
		course.forEach(c -> {
			sum = sum + c.getTimeDuration();
			cant = cant + 1;
			
			if(nomp != c.getCodTeacher())
			{
				nomp = c.getCodTeacher();
				sump ++;
				
			}
			
		});
		
		
		
		return cMono.flatMap(c1 -> {
			Mono<CareerLine> c2 = WebClient
					.builder()
					.baseUrl("http://localhost:3030/api/career/")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.build().get().uri("/select2/"+c1.getCodCareerLine()).retrieve()
					.bodyToMono(CareerLine.class);
			
			Mono<Boolean> c3 = c2.hasElement();
			
			return c3.flatMap(c4 -> {
				if(!c4)
				{
					throw new RequestException("Linea de carrera no existe");
				}
				else
				{ 
					Mono<Teacher> c5 = WebClient
							.builder()
							.baseUrl("http://localhost:3032/api/teacher/")
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.build().get().uri("/select2/"+c1.getCodTeacher()).retrieve()
							.bodyToMono(Teacher.class);
					
					Mono<Boolean> c6 = c5.hasElement();
					
					return c6.flatMap(c7 -> {
						if(c7)
						{
							if(sum < 100)
							{
								throw new RequestException("No completa las 100 horas solicitadas para la línea de carrera");
							}
							else
							{
								if(sump > 2)
								{
								throw new RequestException("El profesor no puede dictar en más de 3 cursos");
								}else
								{
									return cdao.save(c1);
								}
							}
						}
						else {
							throw new RequestException("No existe profesor");
						}
						
					});
					
				}
			});
			
		});
	}
	
	@Override
	public Mono<Void> deleteCourse(Course course) {
		return cdao.delete(course);
	}

}
