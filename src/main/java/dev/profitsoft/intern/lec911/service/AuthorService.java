package dev.profitsoft.intern.lec911.service;

import dev.profitsoft.intern.lec911.dto.author.AuthorDetailsDto;

import java.util.List;

public interface AuthorService {

    List<AuthorDetailsDto> findAll();

}
