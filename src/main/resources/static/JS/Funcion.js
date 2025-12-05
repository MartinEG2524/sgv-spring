document.addEventListener('click', function (e) {
    const btn = e.target.closest('.ver');
    if (!btn) return;

    const td = btn.closest('td');
    const masked = td.querySelector('.masked');
    const real = td.querySelector('.real');

    const showing = real.style.display !== 'none';
    if (showing) {
        real.style.display = 'none';
        masked.style.display = 'inline';
        btn.textContent = 'Mostrar';
    } else {
        masked.style.display = 'none';
        real.style.display = 'inline';
        btn.textContent = 'Ocultar';
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const inputBusqueda = document.getElementById('buscar-usuario');
    if (!inputBusqueda) return;

    inputBusqueda.addEventListener('input', function () {
    const termino = this.value.toLowerCase().trim();
    const filas = document.querySelectorAll('table tbody tr');

    filas.forEach(function (fila) {

    const columnas = fila.querySelectorAll('td');
    let textoFila = '';

    [0, 1, 3, 4].forEach(i => {
        if (columnas[i]) textoFila += columnas[i].textContent.toLowerCase() + ' ';
    });

    fila.style.display = textoFila.includes(termino) ? '' : 'none';
    });
    });
});