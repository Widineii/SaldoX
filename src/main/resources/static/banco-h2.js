const listaTabelas = document.querySelector("#listaTabelas");
const cabecalhoBanco = document.querySelector("#cabecalhoBanco");
const linhasBanco = document.querySelector("#linhasBanco");
const dbStatus = document.querySelector("#dbStatus");
const toastBanco = document.querySelector("#toast");

function avisar(texto, tipo = "success") {
    toastBanco.className = `toast show ${tipo}`;
    toastBanco.textContent = texto;
    window.clearTimeout(avisar.timer);
    avisar.timer = window.setTimeout(() => {
        toastBanco.className = "toast";
    }, 3000);
}

async function carregarTabelas() {
    try {
        dbStatus.textContent = "Online";
        const resposta = await fetch("/banco-h2/tabelas");
        const tabelas = await resposta.json();
        listaTabelas.innerHTML = "";

        tabelas.forEach((tabela, index) => {
            const botao = document.createElement("button");
            botao.type = "button";
            botao.textContent = tabela;
            botao.className = index === 0 ? "active" : "";
            botao.addEventListener("click", () => selecionarTabela(botao, tabela));
            listaTabelas.appendChild(botao);
        });

        if (tabelas.length > 0) {
            await carregarDados(tabelas[0]);
        }
    } catch (erro) {
        dbStatus.textContent = "Offline";
        avisar("Nao foi possivel carregar o banco.", "error");
    }
}

async function selecionarTabela(botao, tabela) {
    document.querySelectorAll(".db-tabs button").forEach((item) => item.classList.remove("active"));
    botao.classList.add("active");
    await carregarDados(tabela);
}

async function carregarDados(tabela) {
    const resposta = await fetch(`/banco-h2/dados?tabela=${encodeURIComponent(tabela)}`);
    const dados = await resposta.json();
    const colunas = dados.length ? Object.keys(dados[0]) : [];

    cabecalhoBanco.innerHTML = `<tr>${colunas.map((coluna) => `<th>${coluna}</th>`).join("")}</tr>`;
    linhasBanco.innerHTML = dados.map((linha) => `
        <tr>${colunas.map((coluna) => `<td>${linha[coluna] ?? ""}</td>`).join("")}</tr>
    `).join("");

    if (dados.length === 0) {
        linhasBanco.innerHTML = '<tr><td class="empty">Tabela vazia.</td></tr>';
    }

    avisar(`${tabela}: ${dados.length} registro(s) carregado(s).`);
}

document.querySelector("#recarregarBanco").addEventListener("click", carregarTabelas);
carregarTabelas();
