package com.uniquindio.prog.Controller;

import com.uniquindio.prog.Entity.Producto;
import com.uniquindio.prog.Service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin("*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Obtiene todos los productos
     * GET /api/productos
     * @return Lista de todos los productos
     */
    @GetMapping // http://localhost:8080/api/productos
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoService.findAll();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene un producto por su ID
     * GET /api/productos/{id}
     * @param id ID del producto
     * @return Producto encontrado o 404 si no existe
     */
    @GetMapping("/{id}") // http://localhost:8080/api/productos/1
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.findById(id);
        if (producto.isPresent()) {
            return ResponseEntity.ok(producto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo producto
     * POST /api/productos
     * @param producto Producto a crear
     * @return Producto creado
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        try {
            // Validar que los campos requeridos no estén vacíos
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            Producto productoGuardado = productoService.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualiza un producto existente
     * PUT /api/productos/{id}
     * @param id ID del producto a actualizar
     * @param productoDetalles Detalles del producto a actualizar
     * @return Producto actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        try {
            Optional<Producto> productoOptional = productoService.findById(id);
            
            if (productoOptional.isPresent()) {
                Producto producto = productoOptional.get();
                
                // Validar que los campos requeridos no estén vacíos
                if (productoDetalles.getNombre() != null && !productoDetalles.getNombre().trim().isEmpty()) {
                    producto.setNombre(productoDetalles.getNombre());
                }
                if (productoDetalles.getPrecio() != null && productoDetalles.getPrecio() > 0) {
                    producto.setPrecio(productoDetalles.getPrecio());
                }
                
                Producto productoActualizado = productoService.save(producto);
                return ResponseEntity.ok(productoActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina un producto por su ID
     * DELETE /api/productos/{id}
     * @param id ID del producto a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        try {
            if (productoService.existsById(id)) {
                productoService.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
