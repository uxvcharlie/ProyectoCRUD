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

    @FXML
    private void configurarTableView(){
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
        colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarca()));
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colFechaCreacion.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFechaCreacion()));
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
        cmbOrdenado.setItems(FXCollections.observableArrayList("Nombre", "Precio menor a mayor", "Precio mayor a menor", "Fecha mas reciente", "Fecha mas antigua", "Stock mayor a menor", "Stock menpor a mayor"));

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

    private void cargarProductos() {}
}
