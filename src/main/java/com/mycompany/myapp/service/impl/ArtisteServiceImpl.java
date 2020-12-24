package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.ArtisteService;
import com.mycompany.myapp.domain.Artiste;
import com.mycompany.myapp.repository.ArtisteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Artiste}.
 */
@Service
@Transactional
public class ArtisteServiceImpl implements ArtisteService {

    private final Logger log = LoggerFactory.getLogger(ArtisteServiceImpl.class);

    private final ArtisteRepository artisteRepository;

    public ArtisteServiceImpl(ArtisteRepository artisteRepository) {
        this.artisteRepository = artisteRepository;
    }

    @Override
    public Artiste save(Artiste artiste) {
        log.debug("Request to save Artiste : {}", artiste);
        return artisteRepository.save(artiste);
    }

    @Override
    public Optional<Artiste> partialUpdate(Artiste artiste) {
        log.debug("Request to partially update Artiste : {}", artiste);

return artisteRepository.findById(artiste.getId())
   .map(existingArtiste -> {
   if (artiste.getFirstName() != null) {
      existingArtiste.setFirstName(artiste.getFirstName());
   }

   if (artiste.getLastName() != null) {
      existingArtiste.setLastName(artiste.getLastName());
   }

   if (artiste.getDateNaiss() != null) {
      existingArtiste.setDateNaiss(artiste.getDateNaiss());
   }


   return existingArtiste;
   })
   .map(artisteRepository::save)
   ;

    }

    @Override
    @Transactional(readOnly = true)
    public Page<Artiste> findAll(Pageable pageable) {
        log.debug("Request to get all Artistes");
        return artisteRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Artiste> findOne(Long id) {
        log.debug("Request to get Artiste : {}", id);
        return artisteRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Artiste : {}", id);
        artisteRepository.deleteById(id);
    }
}
