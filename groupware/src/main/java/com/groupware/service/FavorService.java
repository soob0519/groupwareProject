package com.groupware.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.groupware.entity.FavorDto;
import com.groupware.repository.FavorRepository;

@Service
public class FavorService {

private final FavorRepository favorRepository;

	public FavorService(FavorRepository favorRepository) {
		this.favorRepository = favorRepository;
	}

	/**
	 * 내가 즐겨찾기한 fempno 리스트 반환1
	 */
	public List<Integer> getFavorites(int empno) {
	    return favorRepository.findByEmpno(empno).stream()
	            .map(FavorDto::getFempno)
	            .collect(Collectors.toList());
	}

	public List<Integer> findFavoritesByEmpno(int empno) {
        return favorRepository.findByEmpno(empno)
                              .stream()
                              .map(FavorDto::getFempno)
                              .collect(Collectors.toList());
    }

	public boolean isFavorite(Integer empno, int fempno) {
        return favorRepository.existsByEmpnoAndFempno(empno, fempno);
    }

	public void addFavorite(Integer empno, int fempno) {
	    if (!isFavorite(empno, fempno)) {
	        favorRepository.save(FavorDto.builder()
	            .empno(empno)
	            .fempno(fempno)
	            .build());
	    }
	}

    public void removeFavorite(Integer empno, int fempno) {
        favorRepository.findByEmpnoAndFempno(empno, fempno)
            .ifPresent(favorRepository::delete);
    }


}