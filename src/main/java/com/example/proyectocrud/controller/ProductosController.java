package com.example.proyectocrud.controller;

import com.example.proyectocrud.database.ConexionDB;
import com.example.proyectocrud.model.Categoria;
import com.example.proyectocrud.model.Marca;
import com.example.proyectocrud.model.Producto;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.w3c.dom.Text;

import javax.xml.catalog.CatalogResolver;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductosController {


    //Gestion de productos
    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private ComboBox<Marca> cmbMarca;

    @FXML
    private DatePicker dtpFechaCreacion;


    //Busqueda y filtros
    @FXML
    private TextField txtCodigoNombre;

    @FXML
    private ComboBox<Categoria> cmbCategoriaFiltro;

    @FXML
    private DatePicker dtpDesde;

    @FXML
    private DatePicker dtpHasta;

    @FXML
    private TextField txtPrecioMinimo;

    @FXML
    private TextField txtPrecioMaximo;

    @FXML
    private CheckBox chkExistencia;

    @FXML
    private ComboBox<String> cmbOrdenado;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TableColumn<Producto, LocalDate> colFechaCreacion;

    //Listas observables
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategoria = FXCollections.observableArrayList();
    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();

    /*
     * Formato de fecha dia/mes/año. Sin esto el DatePicker usa el formato
     * del sistema (en_US = mes/dia/año) y "09/10/2026" se lee como 10 de septiembre
     * */
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_ENTRADA = DateTimeFormatter.ofPattern("d/M/yyyy");

    /*
     * JavaFX lo llama automaticamente al cargar el FXML
     * */

    @FXML
    private void initialize(){
        configurarFormatoFechas();
        configurarTableView();
        configurarSeleccionTableView();
        cargarCategorias();
        cargarMarcas();
        cargarOpcionesOrden();
        cargarProductos();
        dtpFechaCreacion.setValue(LocalDate.now());
    }

    @FXML
    private void configurarTableView(){
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
        colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarca()));
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colFechaCreacion.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFechaCreacion()));

        /*
         * Mostramos la fecha en la tabla con el mismo formato de los DatePicker
         * */
        colFechaCreacion.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : FORMATO_FECHA.format(fecha));
            }
        });

        tblProductos.setItems(productos);
    }

    /*
     * Aplica el formato dd/MM/yyyy a todos los DatePicker del formulario
     * */

    private void configurarFormatoFechas() {
        StringConverter<LocalDate> convertidor = new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : FORMATO_FECHA.format(fecha);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                return LocalDate.parse(texto.trim(), FORMATO_FECHA_ENTRADA);
            }
        };

        for (DatePicker datePicker : List.of(dtpFechaCreacion, dtpDesde, dtpHasta)) {
            datePicker.setConverter(convertidor);
            datePicker.setPromptText("dd/mm/aaaa");
        }
    }

    @FXML
    private void configurarSeleccionTableView() {
        tblProductos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarProductoEnFormulario(newValue);
            }
        });
    }

    /*
     * Recibe como parametro un producto seleccionado del TableView
     * @param producto, es el producto seleccionado del TableView
     * */

    private void cargarProductoEnFormulario(Producto p){
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        dtpFechaCreacion.setValue(p.getFechaCreacion());

        /*
         * Buscamos dentro del ComboBox el valor que recorremos al ID
         */

        for(Categoria categoria: listaCategoria){
            if(categoria.getIdCategoria() == p.getIdCategoria()){
                cmbCategoria.setValue(categoria);
                break;
            }
        }

        /*
         * Buscamos dentro del ComboBox el valor que corresponda al ID
         * */

        for(Marca marca : listaMarcas){
            if(marca.getIdMarca() == p.getIdMarca()){
                cmbMarca.setValue(marca);
                break;
            }
        }
    }

    /*
     * Consulta las categorias activas de la base de datos para mostrarlas dentro del ComboBox
     * */

    private void cargarCategorias(){
        listaCategoria.clear();
        String sql = "SELECT id_categoria, nombre FROM catalogos.categorias WHERE estado = 1 " +
                "ORDER BY nombre DESC;";

        try (
                Connection conexion = ConexionDB.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()){
                Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nombre"));
                listaCategoria.add(categoria);
            }

            /*
            * Utilizamos copias de la lista para evitar que
            * cada ComboBox tenga problemas de seleccion
            * */


            cmbCategoria.setItems(FXCollections.observableArrayList(listaCategoria));

            cmbCategoriaFiltro.setItems(FXCollections.observableArrayList(listaCategoria));

        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Error al cargar las categorias");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }


    private void cargarMarcas(){
        listaMarcas.clear();
        String sql = "SELECT id_marcas, nombre FROM catalogos.marcas WHERE estado = 1 " +
                "ORDER BY nombre DESC;";

        try (
                Connection conexion = ConexionDB.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()){
                Marca marca = new Marca(rs.getInt("id_marcas"), rs.getString("nombre"));
                listaMarcas.add(marca);
            }

            /*
             * Utilizamos copias de la lista para evitar que
             * cada ComboBox tenga problemas de seleccion
             * */


            cmbMarca.setItems(FXCollections.observableArrayList(listaMarcas));

        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Error al cargar las marcas");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }


    private void cargarOpcionesOrden(){
        cmbOrdenado.setItems(FXCollections.observableArrayList("Nombre", "Precio menor a mayor", "Precio mayor a menor", "Fecha mas reciente", "Fecha mas antigua", "Stock mayor a menor", "Stock menor a mayor"));

        cmbOrdenado.setValue("Nombre");

    }

    private void mostrarAlerta(
            Alert.AlertType tipo, String titulo, String mensaje
    ){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarErrorBaseDatos(String mensaje, SQLException e){
        e.printStackTrace();

        mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", mensaje + "\n\n" + "Detalle: "+ e.getMessage()
        );
    }


    @FXML
    private void guardar() {

        if (!validarFormulario()) {
            return;
        }


        String codigo =
                txtCodigo.getText().trim();


        /*
         * Validación preventiva de UNIQUE.
         */
        if (existeCodigo(codigo, null)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código " + codigo +
                            " ya pertenece a otro producto."
            );

            txtCodigo.requestFocus();

            return;
        }


        String sql = """
                INSERT INTO catalogos.productos
                (
                    codigo,
                    nombre,
                    descripcion,
                    precio,
                    stock,
                    fecha_creacion,
                    id_categoria,
                    id_marcas,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """;


        try (
                Connection conexion =
                        ConexionDB.getConexion();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    codigo
            );

            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );

            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );

            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );

            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );

            ps.setDate(
                    6,
                    Date.valueOf(
                            dtpFechaCreacion.getValue()
                    )
            );

            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );

            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );


            int filasAfectadas =
                    ps.executeUpdate();


            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro guardado",
                        "El producto fue registrado correctamente."
                );

                limpiarFormulario();

                cargarProductos();
            }

        } catch (SQLException e) {

            /*
             * Aunque JavaFX realizó previamente la comprobación,
             * PostgreSQL continúa siendo la autoridad final.
             *
             * SQLState 23505 corresponde normalmente a una
             * violación de UNIQUE en PostgreSQL.
             */
            if ("23505".equals(e.getSQLState())) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Registro duplicado",
                        "El código ingresado ya existe."
                );

            } else {

                mostrarErrorBaseDatos(
                        "No se pudo guardar el producto.",
                        e
                );
            }
        }
    }

    /*
     * Valida los campos obligatorios del formulario antes de guardar o actualizar.
     * Las reglas coinciden con los CHECK de la tabla productos:
     * precio > 0 y stock >= 0
     * */

    private boolean validarFormulario(){

        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Ingrese el código del producto.");
            txtCodigo.requestFocus();
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Ingrese el nombre del producto.");
            txtNombre.requestFocus();
            return false;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());

            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "El precio debe ser mayor que cero.");
                txtPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Ingrese un precio numérico, por ejemplo 25.50");
            txtPrecio.requestFocus();
            return false;
        }

        try {
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "El stock no puede ser negativo.");
                txtStock.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "Ingrese un stock entero, por ejemplo 10");
            txtStock.requestFocus();
            return false;
        }

        if (dtpFechaCreacion.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Seleccione la fecha de creación.");
            dtpFechaCreacion.requestFocus();
            return false;
        }

        if (cmbCategoria.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Seleccione una categoría.");
            cmbCategoria.requestFocus();
            return false;
        }

        if (cmbMarca.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Seleccione una marca.");
            cmbMarca.requestFocus();
            return false;
        }

        return true;
    }

    /*
     * Verifica si el código ya pertenece a otro producto.
     * @param codigo, es el código a buscar
     * @param idProductoExcluir, es el id del producto que se está actualizando
     * para no compararlo consigo mismo. Al guardar uno nuevo se envía null
     * */

    private boolean existeCodigo(String codigo, Integer idProductoExcluir){
        String sql = "SELECT COUNT(*) FROM catalogos.productos WHERE codigo = ?";

        if (idProductoExcluir != null) {
            sql += " AND id_producto <> ?";
        }

        try (
                Connection conexion = ConexionDB.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setString(1, codigo);

            if (idProductoExcluir != null) {
                ps.setInt(2, idProductoExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudo verificar el código del producto.", e);
            return false;
        }
    }

    /*
     * Deja el formulario vacío para registrar un nuevo producto
     * */

    private void limpiarFormulario(){
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();
        dtpFechaCreacion.setValue(LocalDate.now());
        cmbCategoria.setValue(null);
        cmbMarca.setValue(null);

        tblProductos.getSelectionModel().clearSelection();
        txtCodigo.requestFocus();
    }

    /*
     * Consulta los productos activos con el nombre de su categoria y marca
     * y los muestra en el TableView
     * */

    private void cargarProductos() {
        productos.clear();
        String sql = """
                SELECT p.id_producto, p.codigo, p.nombre, p.descripcion,
                       p.precio, p.stock, p.fecha_creacion, p.estado,
                       p.id_categoria, c.nombre AS categoria,
                       p.id_marcas, m.nombre AS marca
                FROM catalogos.productos p
                INNER JOIN catalogos.categorias c ON c.id_categoria = p.id_categoria
                INNER JOIN catalogos.marcas m ON m.id_marcas = p.id_marcas
                WHERE p.estado = 1
                ORDER BY p.nombre
                """;

        try (
                Connection conexion = ConexionDB.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                productos.add(crearProductoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudieron cargar los productos.", e);
        }
    }

    @FXML
    private void nuevo() {
        limpiarFormulario();
    }

    @FXML
    private void actualizar() {}

    @FXML
    private void eliminar() {}

    /*
     * Deja los filtros de busqueda en su estado inicial
     * */

    @FXML
    private void limpiar() {
        txtCodigoNombre.clear();
        cmbCategoriaFiltro.setValue(null);
        dtpDesde.setValue(null);
        dtpHasta.setValue(null);
        txtPrecioMinimo.clear();
        txtPrecioMaximo.clear();
        chkExistencia.setSelected(false);
        cmbOrdenado.setValue("Nombre");

        cargarProductos();
    }

    private Producto crearProductoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Producto(

                rs.getInt(
                        "id_producto"
                ),

                rs.getString(
                        "codigo"
                ),

                rs.getString(
                        "descripcion"
                ),

                rs.getString(
                        "nombre"
                ),

                rs.getDouble(
                        "precio"
                ),

                rs.getInt(
                        "stock"
                ),

                rs.getDate(
                        "fecha_creacion"
                ).toLocalDate(),

                rs.getInt(
                        "id_categoria"
                ),

                rs.getString(
                        "categoria"
                ),

                rs.getInt(
                        "id_marcas"
                ),

                rs.getString(
                        "marca"
                ),

                rs.getInt(
                        "estado"
                )
        );
    }

    @FXML
    private void buscarProductos() {

        /*
         * La consulta inicia con los INNER JOIN necesarios
         * y con una condición permanente:
         *
         * p.estado = 1
         *
         * De esta forma nunca mostramos productos
         * eliminados lógicamente.
         */
        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.descripcion,
                    p.precio,
                    p.stock,
                    p.fecha_creacion,
                    c.id_categoria,
                    c.nombre AS categoria,
                    m.id_marcas,
                    m.nombre AS marca,
                    p.estado
                FROM catalogos.productos p
                INNER JOIN catalogos.categorias c
                    ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas m
                    ON p.id_marcas = m.id_marcas
                WHERE p.estado = 1
                """);
        /*
         * Aquí almacenaremos los valores que posteriormente
         * serán enviados al PreparedStatement.
         */
        List<Object> parametros =
                new ArrayList<>();

        // Filtro por código o por nombre
        String texto =
                txtCodigoNombre.getText().trim();

        if (!texto.isBlank()) {

            /*
             * OR permite que el texto coincida con código
             * o nombre.
             *
             * ILIKE permite búsquedas parciales sin importar
             * mayúsculas y minúsculas.
             */
            sql.append("""            
                    AND (
                        p.codigo ILIKE ?
                        OR p.nombre ILIKE ?
                    )
                    """);

            parametros.add(
                    "%" + texto + "%"
            );

            parametros.add(
                    "%" + texto + "%"
            );
        }

        // Filtro por categoria
        Categoria categoriaFiltro =
                cmbCategoriaFiltro.getValue();

        if (categoriaFiltro != null) {
            sql.append("""
                    AND p.id_categoria = ?
                    """);
            parametros.add(categoriaFiltro.getIdCategoria());
        }

        /*
         * Si el usuario escribio la fecha a mano y no presiono Enter,
         * el DatePicker todavia no tiene el valor. commitValue() lo convierte.
         */
        try {
            dtpDesde.commitValue();
            dtpHasta.commitValue();
        } catch (DateTimeParseException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Fecha inválida",
                    "Escriba las fechas con el formato dd/mm/aaaa, por ejemplo 09/10/2026"
            );
            return;
        }

        // Filtro por fecha desde
        LocalDate fechaDesde =
                dtpDesde.getValue();

        if (fechaDesde != null) {
            sql.append("""
                    AND p.fecha_creacion >= ?
                    """);
            parametros.add(fechaDesde);
        }

        // Filtro por fechahasta
        LocalDate fechaHasta =
                dtpHasta.getValue();

        if (fechaHasta != null) {

            sql.append("""
                    
                    AND p.fecha_creacion <= ?
                    """);

            parametros.add(fechaHasta);
        }

        /*
         * Verificamos que las fechas tengan sentido.
         *
         * "Desde" no debería ser posterior a "Hasta".
         */
        if (
                fechaDesde != null
                        &&
                        fechaHasta != null
                        &&
                        fechaDesde.isAfter(fechaHasta)
        ) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de fechas incorrecto",
                    "La fecha inicial no puede ser posterior a la fecha final."
            );

            return;
        }

        // Filtro por precio minimo y maximo
        BigDecimal precioMinimo = null;
        BigDecimal precioMaximo = null;

        try {
            if (!txtPrecioMinimo.getText().isBlank()) {
                precioMinimo = new BigDecimal(txtPrecioMinimo.getText().trim());
            }

            if (!txtPrecioMaximo.getText().isBlank()) {
                precioMaximo = new BigDecimal(txtPrecioMaximo.getText().trim());
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "Ingrese precios numéricos, por ejemplo 25.50"
            );
            return;
        }

        if (
                precioMinimo != null
                        &&
                        precioMaximo != null
                        &&
                        precioMinimo.compareTo(precioMaximo) > 0
        ) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de precios incorrecto",
                    "El precio mínimo no puede ser mayor que el precio máximo."
            );

            return;
        }

        if (precioMinimo != null) {
            sql.append("""
                    AND p.precio >= ?
                    """);
            parametros.add(precioMinimo);
        }

        if (precioMaximo != null) {
            sql.append("""
                    AND p.precio <= ?
                    """);
            parametros.add(precioMaximo);
        }

        // Filtro de productos con existencia
        if (chkExistencia.isSelected()) {
            sql.append("""
                    AND p.stock > 0
                    """);
        }

        // Aplicación del ORDER BY

        /*
         * ORDER BY no utiliza un parámetro ? para nombres
         * de columnas.
         *
         * Por esta razón utilizamos solamente opciones
         * previamente definidas en el ComboBox.
         *
         * Esto funciona como una whitelist.
         */
        String orden =
                cmbOrdenado.getValue();

        if (orden == null) {
            orden = "Nombre";
        }

        switch (orden) {

            case "Precio menor a mayor" ->
                    sql.append("""
                            
                            ORDER BY p.precio ASC
                            """);

            case "Precio mayor a menor" ->
                    sql.append("""
                            
                            ORDER BY p.precio DESC
                            """);

            case "Fecha mas reciente" ->
                    sql.append("""
                            
                            ORDER BY p.fecha_creacion DESC
                            """);

            case "Fecha mas antigua" ->
                    sql.append("""
                            
                            ORDER BY p.fecha_creacion ASC
                            """);

            case "Stock mayor a menor" ->
                    sql.append("""
                            
                            ORDER BY p.stock DESC
                            """);

            case "Stock menor a mayor" ->
                    sql.append("""
                            
                            ORDER BY p.stock ASC
                            """);

            default ->
                    sql.append("""
                            
                            ORDER BY p.nombre ASC
                            """);
        }

        //Ejecución de la consulta

        try (
                Connection conexion =
                        ConexionDB.getConexion();

                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql.toString()
                        )
        ) {

            /*
             * Recorremos la lista de parámetros para
             * asignarlos dinámicamente al PreparedStatement.
             */
            for (
                    int i = 0;
                    i < parametros.size();
                    i++
            ) {
                Object valor =
                        parametros.get(i);

                if (valor instanceof LocalDate fecha) {

                    ps.setDate(
                            i + 1,
                            Date.valueOf(fecha)
                    );

                } else if (valor instanceof BigDecimal numero) {

                    ps.setBigDecimal(
                            i + 1,
                            numero
                    );

                } else {
                    ps.setObject(
                            i + 1,
                            valor
                    );
                }
            }

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                productos.clear();

                while (rs.next()) {

                    productos.add(
                            crearProductoDesdeResultSet(rs)
                    );
                }
            }

            /*
             * Podemos informar al estudiante/usuario cuando
             * ningún registro satisface las condiciones.
             */
            if (productos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }
        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo realizar la búsqueda.",
                    e
            );
        }
    }
}
