package com.garritas.sgv.service;

import com.garritas.sgv.model.DetalleHistorialInventario;
import com.garritas.sgv.repository.DetalleHistorialInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleHistorialInventarioServiceImpl implements DetalleHistorialInventarioService {

    private final DetalleHistorialInventarioRepository detalleHistorialInventarioRepository;

    public DetalleHistorialInventarioServiceImpl(DetalleHistorialInventarioRepository detalleHistorialInventarioRepository) {
        this.detalleHistorialInventarioRepository = detalleHistorialInventarioRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<DetalleHistorialInventario> listar() {
        return detalleHistorialInventarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DetalleHistorialInventario> buscarPorId(Long id) {
        return detalleHistorialInventarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DetalleHistorialInventario> buscarPorIdHistorial(Long idHistorial) {
        return detalleHistorialInventarioRepository.findByHistorial_IdHistorial(idHistorial);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DetalleHistorialInventario> buscarPorIdProducto(Long idProducto) {
        return detalleHistorialInventarioRepository.findByProducto_IdProducto(idProducto);
    }

    @Transactional(readOnly = true)
    @Override
    public DetalleHistorialInventario guardar(DetalleHistorialInventario detalle) {
        return detalleHistorialInventarioRepository.save(detalle);
    }

    @Override
    public DetalleHistorialInventario actualizar(DetalleHistorialInventario detalle) {
        return detalleHistorialInventarioRepository.save(detalle);
    }

    @Override
    public void eliminar(Long id) {
        if (!detalleHistorialInventarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Detalle no encontrado: " + id);
        }
        detalleHistorialInventarioRepository.deleteById(id);
    }
}
