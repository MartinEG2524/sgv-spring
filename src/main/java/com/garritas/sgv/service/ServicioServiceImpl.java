package com.garritas.sgv.service;

import com.garritas.sgv.model.Servicio;
import com.garritas.sgv.repository.ServicioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioServiceImpl implements ServicioService {
    private final ServicioRepository repo;
    public ServicioServiceImpl(ServicioRepository repo) { this.repo = repo; }

    public List<Servicio> listar() { return repo.findAll(); }
    public Optional<Servicio> buscarPorId(Long id) { return repo.findById(id); }
    public Servicio guardar(Servicio servicio) { return repo.save(servicio); }
    public void eliminar(Long id) { repo.deleteById(id); }
}
