const togglePassword = document.getElementById("togglePassword");

if (togglePassword) {
  togglePassword.addEventListener("click", () => {
    const passwordInput = document.getElementById("password");
    const icon = togglePassword.querySelector("i");
    if (!passwordInput) return;

    if (passwordInput.type === "password") {
      passwordInput.type = "text";
      icon.classList.remove("fa-eye");
      icon.classList.add("fa-eye-slash");
    } else {
      passwordInput.type = "password";
      icon.classList.remove("fa-eye-slash");
      icon.classList.add("fa-eye");
    }
  });
}

function cerrarSesion() {
  window.location.href = "/logout";
}

if (document.getElementById("inventoryTable")) {
  let productos = [];

  const tableBody = document.getElementById("inventoryTable");
  const searchInput = document.getElementById("searchInput");
  const categoryFilter = document.getElementById("categoryFilter");
  const statusFilter = document.getElementById("statusFilter");
  const modal = document.getElementById("productModal");
  const modalTitle = document.getElementById("modalTitle");
  const saveProductBtn = document.getElementById("saveProductBtn");
  const productId = document.getElementById("productId");
  const newCategoryInput = document.getElementById("newCategoryInput");
  const newCategorySelect = document.getElementById("newCategory");
  let categorias = [];
  let productoEditandoId = null;

  function obtenerEstado(stock) {
    if (stock <= 10) return "Crítico";
    if (stock <= 25) return "Bajo stock";
    return "Disponible";
  }

  function obtenerClaseEstado(estado) {
    if (estado === "Disponible") return "disponible";
    if (estado === "Bajo stock") return "bajo";
    return "critico";
  }

  function renderProductos() {
    const busqueda = searchInput ? searchInput.value.toLowerCase() : "";
    const categoria = categoryFilter ? categoryFilter.value : "Todos";
    const estadoSeleccionado = statusFilter ? statusFilter.value : "Todos";

    tableBody.innerHTML = "";

    const productosFiltrados = productos.filter((producto) => {
      const estado = obtenerEstado(producto.stock);

      const coincideBusqueda = producto.nombre.toLowerCase().includes(busqueda);
      const coincideCategoria = categoria === "Todos" || producto.categoria === categoria;
      const coincideEstado = estadoSeleccionado === "Todos" || estado === estadoSeleccionado;

      return coincideBusqueda && coincideCategoria && coincideEstado;
    });

    if (productosFiltrados.length === 0) {
      const filaVacia = document.createElement("tr");
      filaVacia.innerHTML = `
        <td colspan="7" style="text-align:center; color:#6b7280;">
          No se encontraron productos con esa búsqueda.
        </td>
      `;
      tableBody.appendChild(filaVacia);
    } else {
      productosFiltrados.forEach((producto) => {
        const estado = producto.estado || obtenerEstado(producto.stock);
        const clase = obtenerClaseEstado(estado);

        const fila = document.createElement("tr");

        fila.innerHTML = `
          <td>${producto.nombre}</td>
          <td>${producto.categoria}</td>
          <td>${producto.stock}</td>
          <td>${producto.ubicacion}</td>
          <td>${producto.rfid}</td>
          <td><span class="badge ${clase}">${estado}</span></td>
          <td>
            <button class="btn-action" type="button" data-id="${producto.id}" onclick="abrirModalParaEditar(this.getAttribute('data-id'))">
              <i class="fa-solid fa-pen"></i>
            </button>
          </td>
        `;

        tableBody.appendChild(fila);
      });
    }

    actualizarResumen();
  }

  function actualizarResumen() {
    const totalEl = document.getElementById("totalProductos");
    const bajoEl = document.getElementById("bajoStock");
    const criticoEl = document.getElementById("stockCritico");

    if (totalEl) totalEl.textContent = productos.length;
    if (bajoEl) bajoEl.textContent = productos.filter((p) => obtenerEstado(p.stock) === "Bajo stock").length;
    if (criticoEl) criticoEl.textContent = productos.filter((p) => obtenerEstado(p.stock) === "Crítico").length;
  }

  function abrirModal() {
    productoEditandoId = null;
    if (modalTitle) modalTitle.textContent = "Agregar producto";
    if (saveProductBtn) saveProductBtn.textContent = "Guardar producto";
    if (productId) productId.value = "";
    limpiarFormulario();
    if (modal) modal.classList.add("active");
  }

  function cerrarModal() {
    if (modal) modal.classList.remove("active");
    productoEditandoId = null;
  }

  function llenarCategorias() {
    const categoriasUnicas = [...new Set(productos.map((producto) => producto.categoria).filter(Boolean))];
    categorias = categoriasUnicas;

    if (newCategorySelect) {
      newCategorySelect.innerHTML = '<option value="">Seleccionar categoría existente</option>';
      categoriasUnicas.forEach((categoria) => {
        const option = document.createElement("option");
        option.value = categoria;
        option.textContent = categoria;
        newCategorySelect.appendChild(option);
      });
    }

    const categoryFilter = document.getElementById("categoryFilter");
    if (categoryFilter) {
      const valorActual = categoryFilter.value;
      categoryFilter.innerHTML = '<option value="Todos">Todas las categorías</option>';
      categoriasUnicas.forEach((categoria) => {
        const option = document.createElement("option");
        option.value = categoria;
        option.textContent = categoria;
        categoryFilter.appendChild(option);
      });
      if (categoriasUnicas.includes(valorActual)) {
        categoryFilter.value = valorActual;
      }
    }
  }

  function abrirModalParaEditar(id) {
    const producto = productos.find((item) => String(item.id) === String(id));
    if (!producto) return;

    productoEditandoId = producto.id;
    if (modalTitle) modalTitle.textContent = "Editar producto";
    if (saveProductBtn) saveProductBtn.textContent = "Actualizar producto";
    if (productId) productId.value = producto.id;

    const newNameEl = document.getElementById("newName");
    const newStockEl = document.getElementById("newStock");
    const newLocationEl = document.getElementById("newLocation");
    const newRfidEl = document.getElementById("newRfid");

    if (newNameEl) newNameEl.value = producto.nombre || "";
    if (newCategoryInput) newCategoryInput.value = producto.categoria || "";
    if (newCategorySelect) newCategorySelect.value = producto.categoria || "";
    if (newStockEl) newStockEl.value = producto.stock ?? "";
    if (newLocationEl) newLocationEl.value = producto.ubicacion || "";
    if (newRfidEl) newRfidEl.value = producto.rfid || "";

    if (modal) modal.classList.add("active");
  }

  async function agregarProducto() {
    const newNameEl = document.getElementById("newName");
    const newStockEl = document.getElementById("newStock");
    const newLocationEl = document.getElementById("newLocation");
    const newRfidEl = document.getElementById("newRfid");

    const nombre = newNameEl ? newNameEl.value.trim() : "";
    const categoria = newCategoryInput && newCategoryInput.value.trim()
      ? newCategoryInput.value.trim()
      : (newCategorySelect ? newCategorySelect.value : "");
    const stock = newStockEl ? Number(newStockEl.value) : 0;
    const ubicacion = newLocationEl ? newLocationEl.value.trim() : "";
    const rfid = newRfidEl ? newRfidEl.value.trim() : "";
    const estado = stock <= 10 ? "Crítico" : stock <= 25 ? "Bajo stock" : "Disponible";

    if (!nombre || !categoria || !Number.isFinite(stock) || !ubicacion || !rfid) {
      alert("Completa todos los campos.");
      return;
    }

    const payload = { nombre, categoria, stock, ubicacion, rfid, estado };

    try {
      const url = productoEditandoId ? `/api/productos/${productoEditandoId}` : "/api/productos";
      const method = productoEditandoId ? "PUT" : "POST";
      await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      cerrarModal();
      limpiarFormulario();
      await cargarProductos();
    } catch (error) {
      console.error("Error guardando producto:", error);
      alert("No se pudo guardar el producto.");
    }
  }

  function limpiarFormulario() {
    const ids = ["newName", "newStock", "newLocation", "newRfid", "newCategoryInput", "newCategory"];
    ids.forEach((id) => {
      const el = document.getElementById(id);
      if (el) el.value = "";
    });
    if (newCategorySelect) newCategorySelect.value = "";
  }

  async function cargarProductos() {
    try {
      const resp = await fetch("/api/productos");
      productos = await resp.json();
      llenarCategorias();
      renderProductos();
    } catch (error) {
      console.error("Error cargando productos:", error);
      renderProductos();
    }
  }

  if (searchInput) {
    searchInput.addEventListener("input", renderProductos);
  }

  if (categoryFilter) {
    categoryFilter.addEventListener("change", renderProductos);
  }

  if (statusFilter) {
    statusFilter.addEventListener("change", renderProductos);
  }

  cargarProductos();

  // Hacer funciones globales que se usan en HTML
  window.abrirModal = abrirModal;
  window.cerrarModal = cerrarModal;
  window.agregarProducto = agregarProducto;
  window.abrirModalParaEditar = abrirModalParaEditar;
}

if (document.getElementById("pedidoModal")) {
  const ordersTable = document.getElementById("ordersTable");
  const orderModal = document.getElementById("pedidoModal");
  const pendingOrders = document.getElementById("pendingOrders");
  const preparingOrders = document.getElementById("preparingOrders");
  const dispatchedOrders = document.getElementById("dispatchedOrders");
  const deliveredOrders = document.getElementById("deliveredOrders");
  const pendingCount = document.getElementById("pendingCount");
  const preparingCount = document.getElementById("preparingCount");
  const dispatchedCount = document.getElementById("dispatchedCount");
  const deliveredCount = document.getElementById("deliveredCount");
  const orderForm = {
    codigo: document.getElementById("newOrderCode"),
    cliente: document.getElementById("newOrderClient"),
    totalProductos: document.getElementById("newOrderTotal"),
    fechaPedido: document.getElementById("newOrderDate"),
    estado: document.getElementById("newOrderStatus"),
    canal: document.getElementById("newOrderChannel"),
    operario: document.getElementById("newOrderOperario")
  };
  const orderIdInput = document.getElementById("pedidoId");
  const orderModalTitle = document.getElementById("pedidoModalTitle");
  const saveOrderBtn = document.getElementById("saveOrderBtn");
  let pedidos = [];
  let operarios = [];
  let pedidoEditandoId = null;

  function renderPedidos() {
    if (ordersTable) {
      ordersTable.innerHTML = "";
      pedidos.forEach((pedido) => {
        const operarioNombre = pedido.operario ? pedido.operario.nombre : "Sin asignar";
        const fecha = pedido.fechaPedido ? new Date(pedido.fechaPedido).toLocaleString() : "Sin fecha";

        const fila = document.createElement("tr");
        fila.innerHTML = `
          <td>${pedido.codigo}</td>
          <td>${pedido.cliente}</td>
          <td>${pedido.totalProductos}</td>
          <td>${pedido.canal}</td>
          <td>${pedido.estado}</td>
          <td>${operarioNombre}</td>
          <td>${fecha}</td>
          <td>
            <button class="btn btn-outline-secondary btn-sm" type="button" onclick="abrirPedidoModal(${pedido.id})">
              <i class="fa-solid fa-pen"></i>
            </button>
          </td>
        `;
        ordersTable.appendChild(fila);
      });
    }

    const pedidosPorEstado = {
      Pendiente: pedidos.filter((pedido) => pedido.estado === "Pendiente"),
      "En preparación": pedidos.filter((pedido) => pedido.estado === "En preparación"),
      Despachado: pedidos.filter((pedido) => pedido.estado === "Despachado"),
      Entregado: pedidos.filter((pedido) => pedido.estado === "Entregado")
    };

    const columnas = [
      { key: "Pendiente", container: pendingOrders, count: pendingCount, iconClass: "red", icon: "fa-cart-shopping" },
      { key: "En preparación", container: preparingOrders, count: preparingCount, iconClass: "yellow", icon: "fa-cart-shopping" },
      { key: "Despachado", container: dispatchedOrders, count: dispatchedCount, iconClass: "blue", icon: "fa-truck" },
      { key: "Entregado", container: deliveredOrders, count: deliveredCount, iconClass: "green", icon: "fa-check" }
    ];

    columnas.forEach(({ key, container, count, iconClass, icon }) => {
      if (!container) return;
      container.innerHTML = "";
      if (count) count.textContent = `(${pedidosPorEstado[key].length})`;

      pedidosPorEstado[key].forEach((pedido) => {
        const operarioNombre = pedido.operario ? pedido.operario.nombre : "Sin asignar";
        const iniciales = operarioNombre === "Sin asignar"
          ? ""
          : operarioNombre.split(" ").slice(0, 2).map((parte) => parte[0]).join("").toUpperCase();
        const fecha = pedido.fechaPedido ? new Date(pedido.fechaPedido).toLocaleString() : "Sin fecha";

        const card = document.createElement("div");
        card.className = "order-card";
        card.innerHTML = `
          <div class="order-header">
            <h3>${pedido.codigo}</h3>
            <span class="icon ${iconClass}">
              <i class="fa-solid ${icon}"></i>
            </span>
          </div>
          <h4>${pedido.cliente}</h4>
          <p>${pedido.totalProductos} productos</p>
          <p>${fecha}</p>
          <div class="operator">
            <span class="avatar ${operarioNombre === "Sin asignar" ? "empty" : ""}">${iniciales || '<i class="fa-regular fa-user"></i>'}</span>
            <span>${operarioNombre}</span>
          </div>
          <div class="mt-2 text-end">
            <button class="btn btn-outline-secondary btn-sm" type="button" onclick="abrirPedidoModal(${pedido.id})">
              <i class="fa-solid fa-pen"></i>
            </button>
          </div>
        `;
        container.appendChild(card);
      });
    });
  }

  async function cargarOperariosParaPedido() {
    try {
      const resp = await fetch("/api/operarios");
      operarios = await resp.json();
      if (orderForm.operario) {
        orderForm.operario.innerHTML = `<option value="">Seleccionar operario</option>`;
        operarios.forEach((operario) => {
          const option = document.createElement("option");
          option.value = operario.id;
          option.textContent = operario.nombre;
          orderForm.operario.appendChild(option);
        });
      }
    } catch (error) {
      console.error("Error cargando operarios:", error);
    }
  }

  async function cargarPedidos() {
    try {
      const resp = await fetch("/api/pedidos");
      pedidos = await resp.json();
      renderPedidos();
    } catch (error) {
      console.error("Error cargando pedidos:", error);
    }
  }

  async function agregarPedido() {
    const codigo = orderForm.codigo.value.trim();
    const cliente = orderForm.cliente.value.trim();
    const totalProductos = Number(orderForm.totalProductos.value);
    const fechaPedido = orderForm.fechaPedido.value;
    const estado = orderForm.estado.value;
    const canal = orderForm.canal.value;
    const operarioId = orderForm.operario.value;

    if (!codigo || !cliente || !totalProductos || !fechaPedido || !estado || !canal) {
      alert("Completa todos los campos obligatorios.");
      return;
    }

    const pedido = {
      id: pedidoEditandoId ? Number(pedidoEditandoId) : null,
      codigo,
      cliente,
      totalProductos,
      fechaPedido,
      estado,
      canal,
      operario: operarioId ? { id: Number(operarioId) } : null
    };

    try {
      await fetch("/api/pedidos", {
        method: pedidoEditandoId ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(pedido)
      });
      cerrarPedidoModal();
      limpiarPedidoFormulario();
      await cargarPedidos();
    } catch (error) {
      console.error("Error creando o actualizando pedido:", error);
      alert("No se pudo guardar el pedido.");
    }
  }

  async function abrirPedidoModal(pedidoId = null) {
    if (!orderModal) return;
    if (pedidoId) {
      await cargarOperariosParaPedido();
      const pedido = pedidos.find((item) => String(item.id) === String(pedidoId));
      if (!pedido) return;

      pedidoEditandoId = pedido.id;
      if (orderModalTitle) orderModalTitle.textContent = "Editar pedido";
      if (saveOrderBtn) saveOrderBtn.textContent = "Actualizar pedido";
      if (orderIdInput) orderIdInput.value = pedido.id;
      if (orderForm.codigo) orderForm.codigo.value = pedido.codigo || "";
      if (orderForm.cliente) orderForm.cliente.value = pedido.cliente || "";
      if (orderForm.totalProductos) orderForm.totalProductos.value = pedido.totalProductos ?? "";
      if (orderForm.fechaPedido) {
        const fecha = pedido.fechaPedido ? String(pedido.fechaPedido).replace(" ", "T").slice(0, 16) : "";
        orderForm.fechaPedido.value = fecha;
      }
      if (orderForm.estado) orderForm.estado.value = pedido.estado || "";
      if (orderForm.canal) orderForm.canal.value = pedido.canal || "";
      if (orderForm.operario) orderForm.operario.value = pedido.operario && pedido.operario.id ? String(pedido.operario.id) : "";
    } else {
      limpiarPedidoFormulario();
      if (orderModalTitle) orderModalTitle.textContent = "Agregar pedido";
      if (saveOrderBtn) saveOrderBtn.textContent = "Guardar pedido";
    }

    orderModal.classList.add("active");
  }

  function cerrarPedidoModal() {
    if (orderModal) orderModal.classList.remove("active");
    limpiarPedidoFormulario();
  }

  function limpiarPedidoFormulario() {
    Object.values(orderForm).forEach((input) => {
      if (input) input.value = "";
    });
    if (orderIdInput) orderIdInput.value = "";
    pedidoEditandoId = null;
    if (orderModalTitle) orderModalTitle.textContent = "Agregar pedido";
    if (saveOrderBtn) saveOrderBtn.textContent = "Guardar pedido";
  }

  window.abrirPedidoModal = abrirPedidoModal;
  window.cerrarPedidoModal = cerrarPedidoModal;
  window.agregarPedido = agregarPedido;

  cargarOperariosParaPedido();
  cargarPedidos();
}

if (document.getElementById("operariosTable")) {
  const operariosTable = document.getElementById("operariosTable");
  const operarioModal = document.getElementById("operarioModal");
  const form = {
    nombre: document.getElementById("newOperarioName"),
    puesto: document.getElementById("newOperarioPosition"),
    pedidosAsignados: document.getElementById("newOperarioOrders"),
    eficiencia: document.getElementById("newOperarioEfficiency"),
    estado: document.getElementById("newOperarioStatus"),
    ultimoIngreso: document.getElementById("newOperarioLastLogin")
  };
  let operarios = [];

  function renderOperarios() {
    operariosTable.innerHTML = "";
    operarios.forEach((operario) => {
      const fila = document.createElement("tr");
      const estado = (operario.estado || "Inactivo").toLowerCase();
      const badgeClass = estado === "activo" ? "disponible" : "critico";
      const botonTexto = estado === "activo" ? "Desactivar" : "Activar";
      fila.innerHTML = `
        <td>${operario.nombre}</td>
        <td>${operario.puesto}</td>
        <td>${operario.pedidosAsignados ?? 0}</td>
        <td>${operario.eficiencia ?? "0%"}</td>
        <td><span class="badge ${badgeClass}">${operario.estado || "Inactivo"}</span></td>
        <td>${operario.ultimoIngreso || "Sin registro"}</td>
        <td>
          <button class="btn btn-outline-secondary btn-sm" type="button" onclick="cambiarEstadoOperario(${operario.id})">
            <i class="fa-solid fa-toggle-on"></i> ${botonTexto}
          </button>
        </td>
      `;
      operariosTable.appendChild(fila);
    });
  }

  async function cargarOperarios() {
    try {
      const resp = await fetch("/api/operarios");
      operarios = await resp.json();
      renderOperarios();
    } catch (error) {
      console.error("Error cargando operarios:", error);
    }
  }

  async function agregarOperario() {
    const nombre = form.nombre.value.trim();
    const puesto = form.puesto.value.trim();
    const pedidosAsignados = Number(form.pedidosAsignados.value);
    const eficiencia = Number(form.eficiencia.value);
    const estado = form.estado.value;
    const ultimoIngreso = form.ultimoIngreso.value.trim();

    if (!nombre || !puesto || !pedidosAsignados || !eficiencia || !estado || !ultimoIngreso) {
      alert("Completa todos los campos.");
      return;
    }

    try {
      await fetch("/api/operarios", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre, puesto, pedidosAsignados, eficiencia, estado, ultimoIngreso })
      });
      cerrarOperarioModal();
      limpiarOperarioFormulario();
      cargarOperarios();
    } catch (error) {
      console.error("Error creando operario:", error);
      alert("No se pudo guardar el operario.");
    }
  }

  async function cambiarEstadoOperario(id) {
    const operarioActual = operarios.find((operario) => String(operario.id) === String(id));
    if (!operarioActual) return;

    const nuevoEstado = operarioActual.estado === "Activo" ? "Inactivo" : "Activo";

    try {
      await fetch("/api/operarios", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...operarioActual, estado: nuevoEstado })
      });
      await cargarOperarios();
    } catch (error) {
      console.error("Error cambiando estado del operario:", error);
      alert("No se pudo cambiar el estado del operario.");
    }
  }

  function abrirOperarioModal() {
    if (operarioModal) operarioModal.classList.add("active");
  }

  function cerrarOperarioModal() {
    if (operarioModal) operarioModal.classList.remove("active");
  }

  function limpiarOperarioFormulario() {
    Object.values(form).forEach((input) => {
      if (input) input.value = "";
    });
  }

  window.abrirOperarioModal = abrirOperarioModal;
  window.cerrarOperarioModal = cerrarOperarioModal;
  window.agregarOperario = agregarOperario;
  window.cambiarEstadoOperario = cambiarEstadoOperario;

  cargarOperarios();
}

function inicializarGraficosReportes() {
  const salesCanvas = document.getElementById('salesChart');
  const rotationCanvas = document.getElementById('rotationChart');
  const cancelationCanvas = document.getElementById('cancelationChart');

  if (!salesCanvas || !rotationCanvas || !cancelationCanvas || typeof Chart === 'undefined') return;

  const leerDatos = (canvas) => {
    const labels = (canvas.dataset.labels || '').split(',').map((item) => item.trim()).filter(Boolean);
    const values = (canvas.dataset.values || '').split(',').map((item) => Number(item.trim())).filter((item) => Number.isFinite(item));
    return { labels, values };
  };

  const salesData = leerDatos(salesCanvas);
  const rotationData = leerDatos(rotationCanvas);
  const cancelationData = leerDatos(cancelationCanvas);

  const salesLabels = salesData.labels.length > 0 ? salesData.labels : ['01', '05', '09', '13', '17', '21', '25', '29'];
  const salesValues = salesData.values.length > 0 ? salesData.values : [1200, 1550, 1800, 2100, 2400, 2750, 3100, 3350];
  const rotationLabels = rotationData.labels.length > 0 ? rotationData.labels : ['Abarrotes', 'Lácteos', 'Bebidas', 'Limpieza', 'Frescos'];
  const rotationValues = rotationData.values.length > 0 ? rotationData.values : [4.8, 5.1, 5.4, 4.9, 5.7, 6.0, 5.6];
  const cancelationLabels = cancelationData.labels.length > 0 ? cancelationData.labels : ['Semana 1', 'Semana 2', 'Semana 3', 'Semana 4'];
  const cancelationValues = cancelationData.values.length > 0 ? cancelationData.values : [2.1, 2.3, 2.8, 3.1, 3.5, 3.8];

  new Chart(salesCanvas, {
    type: 'line',
    data: {
      labels: salesLabels,
      datasets: [{
        label: 'Ingresos diarios',
        data: salesValues,
        backgroundColor: 'rgba(47, 128, 237, 0.15)',
        borderColor: 'rgba(47, 128, 237, 0.95)',
        pointBackgroundColor: 'rgba(47, 128, 237, 1)',
        pointBorderColor: '#fff',
        pointRadius: 6,
        pointHoverRadius: 8,
        showLine: false,
        tension: 0.3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          grid: { display: false },
          title: { display: true, text: 'Día' }
        },
        y: {
          grid: { color: '#f0f2f5' },
          title: { display: true, text: 'Ventas (S/.)' }
        }
      },
      plugins: {
        legend: { display: false }
      }
    }
  });

  new Chart(rotationCanvas, {
    type: 'bar',
    data: {
      labels: rotationLabels,
      datasets: [{
        label: 'Rotación',
        data: rotationValues,
        backgroundColor: 'rgba(39, 174, 96, 0.85)',
        borderColor: 'rgba(39, 174, 96, 1)',
        borderWidth: 1,
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          grid: { display: false }
        },
        y: {
          beginAtZero: true,
          grid: { color: '#f0f2f5' },
          title: { display: true, text: 'Veces de rotación' }
        }
      },
      plugins: {
        legend: { display: false }
      }
    }
  });

  new Chart(cancelationCanvas, {
    type: 'line',
    data: {
      labels: cancelationLabels,
      datasets: [{
        label: 'Cancelación',
        data: cancelationValues,
        borderColor: 'rgba(242, 183, 5, 0.95)',
        backgroundColor: 'rgba(242, 183, 5, 0.15)',
        borderWidth: 3,
        fill: true,
        tension: 0.3,
        pointRadius: 5,
        pointBackgroundColor: 'rgba(242, 183, 5, 1)'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          grid: { display: false }
        },
        y: {
          beginAtZero: true,
          grid: { color: '#f0f2f5' },
          title: { display: true, text: 'Cancelación (%)' }
        }
      },
      plugins: {
        legend: { display: false }
      }
    }
  });
}

// Resaltar en la sidebar el enlace activo según la ruta actual
function marcarEnlaceActivo() {
  try {
    const enlaces = document.querySelectorAll('.sidebar nav a');
    if (!enlaces || enlaces.length === 0) return;

    const archivo = (location.pathname || '').split('/').pop();
    enlaces.forEach((a) => {
      const href = a.getAttribute('href') || '';
      const hrefArchivo = href.split('/').pop();
      if (hrefArchivo === archivo) {
        a.classList.add('active');
      } else {
        a.classList.remove('active');
      }
    });
  } catch (e) {
    // no hacer nada si no hay sidebar
  }
}

// Ejecutar al cargar
document.addEventListener('DOMContentLoaded', () => {
  mostrarDatosUsuario();
  marcarEnlaceActivo();
  inicializarGraficosReportes();
});