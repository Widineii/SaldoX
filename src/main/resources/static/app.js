const api = {
    transacoes: "/transacoes",
    authLogin: "/auth/login",
    authRegistrar: "/auth/registrar",
    recuperarSenha: "/auth/recuperar-senha",
    redefinirSenha: "/auth/redefinir-senha"
};

const authScreen = document.querySelector("#authScreen");
const authForm = document.querySelector("#authForm");
const authNome = document.querySelector("#authNome");
const authEmail = document.querySelector("#authEmail");
const authSenha = document.querySelector("#authSenha");
const nomeLabel = document.querySelector("#nomeLabel");
const authSubmit = document.querySelector("#authSubmit");
const alternarAuth = document.querySelector("#alternarAuth");
const recuperarSenhaBotao = document.querySelector("#recuperarSenhaBotao");
const resetBox = document.querySelector("#resetBox");
const resetToken = document.querySelector("#resetToken");
const resetSenha = document.querySelector("#resetSenha");
const confirmarResetSenha = document.querySelector("#confirmarResetSenha");
const authAjuda = document.querySelector("#authAjuda");
const perfilAvatar = document.querySelector("#perfilAvatar");
const perfilNome = document.querySelector("#perfilNome");
const perfilEmail = document.querySelector("#perfilEmail");

const form = document.querySelector("#formulario");
const transacaoId = document.querySelector("#transacaoId");
const descricao = document.querySelector("#descricao");
const valor = document.querySelector("#valor");
const data = document.querySelector("#data");
const vencimento = document.querySelector("#vencimento");
const categoria = document.querySelector("#categoria");
const tipo = document.querySelector("#tipo");
const status = document.querySelector("#status");
const parcelado = document.querySelector("#parcelado");
const quantidadeParcelas = document.querySelector("#quantidadeParcelas");
const cartaoCredito = document.querySelector("#cartaoCredito");
const modo = document.querySelector("#modo");
const listaTransacoes = document.querySelector("#listaTransacoes");
const categoriaLista = document.querySelector("#categoriaLista");
const donutChart = document.querySelector("#donutChart");
const graficoMensal = document.querySelector("#graficoMensal");
const filtroBusca = document.querySelector("#filtroBusca");
const filtroTipo = document.querySelector("#filtroTipo");
const filtroCategoria = document.querySelector("#filtroCategoria");
const filtroMes = document.querySelector("#filtroMes");
const filtroDataInicio = document.querySelector("#filtroDataInicio");
const filtroDataFim = document.querySelector("#filtroDataFim");
const filtroValorMin = document.querySelector("#filtroValorMin");
const filtroValorMax = document.querySelector("#filtroValorMax");
const toast = document.querySelector("#toast");
const maiorDespesa = document.querySelector("#maiorDespesa");
const maiorDespesaTexto = document.querySelector("#maiorDespesaTexto");
const mediaGasto = document.querySelector("#mediaGasto");
const resultadoStatus = document.querySelector("#resultadoStatus");
const resultadoTexto = document.querySelector("#resultadoTexto");
const orcamentoUsado = document.querySelector("#orcamentoUsado");
const orcamentoTexto = document.querySelector("#orcamentoTexto");
const orcamentoBarra = document.querySelector("#orcamentoBarra");
const dicaTexto = document.querySelector("#dicaTexto");
const menuToggle = document.querySelector("#menuToggle");
const fecharDica = document.querySelector("#fecharDica");
const pageTitle = document.querySelector("#pageTitle");
const menuLinks = document.querySelectorAll(".menu a");
const modalConfirmacao = document.querySelector("#modalConfirmacao");
const confirmarExclusao = document.querySelector("#confirmarExclusao");
const cancelarExclusao = document.querySelector("#cancelarExclusao");
const perfilNomeInput = document.querySelector("#perfilNomeInput");
const perfilEmailInput = document.querySelector("#perfilEmailInput");
const perfilSenhaInput = document.querySelector("#perfilSenhaInput");
const avatarInput = document.querySelector("#avatarInput");
const emptyState = document.querySelector("#emptyState");
const metaMensal = document.querySelector("#metaMensal");
const importarJsonInput = document.querySelector("#importarJsonInput");
const loadingOverlay = document.querySelector("#loadingOverlay");

let transacoesAtuais = [];
let authModoRegistro = false;
let usuario = JSON.parse(localStorage.getItem("fintrackUsuario") || "null");
let idPendenteExclusao = null;
let graficoMensalChart = null;
let carregando = 0;

const formatoMoeda = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
});

function hoje() {
    return new Date().toISOString().slice(0, 10);
}

function mesAtual() {
    return new Date().toISOString().slice(0, 7);
}

function aplicarTemaSalvo() {
    const tema = localStorage.getItem("fintrackTema") || "light";
    document.body.classList.toggle("dark-theme", tema === "dark");
    metaMensal.value = localStorage.getItem("fintrackMetaMensal") || "2000";
}

function formatarData(valor) {
    return new Date(`${valor}T00:00:00`).toLocaleDateString("pt-BR");
}

function mostrarMensagem(texto, tipo = "success") {
    toast.className = `toast show ${tipo}`;
    toast.innerHTML = texto;
    window.clearTimeout(mostrarMensagem.timer);
    mostrarMensagem.timer = window.setTimeout(() => {
        toast.className = "toast";
    }, 3000);
}

function escapeHtml(valor) {
    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function transacaoPorId(id) {
    return transacoesAtuais.find((transacao) => String(transacao.id) === String(id));
}

function setLoading(ativo) {
    carregando += ativo ? 1 : -1;
    carregando = Math.max(carregando, 0);
    document.body.classList.toggle("is-loading", carregando > 0);
    loadingOverlay?.setAttribute("aria-hidden", carregando > 0 ? "false" : "true");
}

function authHeader() {
    return usuario?.token ? { Authorization: `Bearer ${usuario.token}` } : {};
}

function jsonHeaders() {
    return {
        "Content-Type": "application/json",
        ...authHeader()
    };
}

async function apiFetch(url, options = {}) {
    const resposta = await fetch(url, {
        ...options,
        headers: {
            ...authHeader(),
            ...(options.headers || {})
        }
    });

    if (resposta.status === 401 || resposta.status === 403) {
        localStorage.removeItem("fintrackUsuario");
        usuario = null;
        authScreen.classList.add("open");
        throw new Error("Sessao expirada. Entre novamente.");
    }

    return resposta;
}

async function mensagemErroApi(resposta, fallback) {
    try {
        const erro = await resposta.json();
        return erro.mensagens?.[0] || erro.erro || fallback;
    } catch (erro) {
        return fallback;
    }
}

function abrirBancoH2(event) {
    event?.preventDefault();
    window.open("/banco-h2.html", "_blank", "noopener");
    mostrarMensagem("Visualizador H2 aberto com as tabelas reais do sistema.");
}

function usuarioLogado() {
    return Boolean(usuario?.usuarioId);
}

function emailPareceReal(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email);
}

function atualizarPerfil() {
    if (!usuarioLogado()) {
        authScreen.classList.add("open");
        return;
    }

    authScreen.classList.remove("open");
    perfilNome.textContent = `Ola, ${usuario.nome}!`;
    perfilEmail.textContent = usuario.email;
    if (usuario.avatarUrl) {
        perfilAvatar.textContent = "";
        const avatar = document.createElement("img");
        avatar.src = usuario.avatarUrl;
        avatar.alt = `Avatar de ${usuario.nome}`;
        perfilAvatar.appendChild(avatar);
    } else {
        perfilAvatar.textContent = usuario.nome.charAt(0).toUpperCase();
    }
    perfilNomeInput.value = usuario.nome;
    perfilEmailInput.value = usuario.email;
}

async function autenticar(event) {
    event.preventDefault();

    if (authModoRegistro && !emailPareceReal(authEmail.value.trim())) {
        mostrarMensagem("Informe um email completo, exemplo: nome@email.com.", "error");
        return;
    }

    const url = authModoRegistro ? api.authRegistrar : api.authLogin;
    const payload = authModoRegistro
        ? { nome: authNome.value.trim(), email: authEmail.value.trim(), senha: authSenha.value }
        : { email: authEmail.value.trim(), senha: authSenha.value };

    try {
        const resposta = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!resposta.ok) {
            const mensagem = authModoRegistro
                ? await mensagemErroApi(resposta, "Nao foi possivel criar a conta.")
                : await mensagemErroApi(resposta, "Conta nao encontrada ou senha incorreta.");
            throw new Error(mensagem);
        }

        usuario = await resposta.json();
        localStorage.setItem("fintrackUsuario", JSON.stringify(usuario));
        atualizarPerfil();
        mostrarMensagem(authModoRegistro ? "Conta criada com sucesso." : "Login realizado com sucesso.");
        await carregarTransacoes();
    } catch (erro) {
        mostrarMensagem(erro.message || "Conta nao encontrada ou senha incorreta.", "error");
    }
}

async function solicitarRecuperacaoSenha() {
    const email = authEmail.value.trim();

    if (!emailPareceReal(email)) {
        mostrarMensagem("Digite seu email para gerar o codigo de recuperacao.", "error");
        authEmail.focus();
        return;
    }

    try {
        const resposta = await fetch(api.recuperarSenha, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        });

        if (!resposta.ok) {
            throw new Error(await mensagemErroApi(resposta, "Nao foi possivel gerar o codigo."));
        }

        const dados = await resposta.json();
        resetBox.hidden = false;
        resetToken.value = dados.token || "";
        authAjuda.innerHTML = `Codigo de recuperacao: <strong>${dados.token}</strong>. Digite a nova senha abaixo.`;
        mostrarMensagem("Codigo de recuperacao gerado.");
    } catch (erro) {
        mostrarMensagem(erro.message, "error");
    }
}

async function confirmarNovaSenha() {
    if (!resetToken.value.trim() || resetSenha.value.length < 6) {
        mostrarMensagem("Informe o codigo e uma senha com pelo menos 6 caracteres.", "error");
        return;
    }

    try {
        const resposta = await fetch(api.redefinirSenha, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                token: resetToken.value.trim(),
                novaSenha: resetSenha.value
            })
        });

        if (!resposta.ok) {
            throw new Error(await mensagemErroApi(resposta, "Nao foi possivel redefinir a senha."));
        }

        resetSenha.value = "";
        resetToken.value = "";
        resetBox.hidden = true;
        mostrarMensagem("Senha redefinida. Agora voce pode entrar.");
    } catch (erro) {
        mostrarMensagem(erro.message, "error");
    }
}

function alternarModoAuth() {
    authModoRegistro = !authModoRegistro;
    nomeLabel.style.display = authModoRegistro ? "grid" : "none";
    authSubmit.textContent = authModoRegistro ? "Criar conta" : "Entrar";
    alternarAuth.textContent = authModoRegistro ? "Ja tenho conta" : "Criar nova conta";
    recuperarSenhaBotao.style.display = authModoRegistro ? "none" : "block";
    resetBox.hidden = true;
    authAjuda.textContent = authModoRegistro
        ? "Cadastre seus dados para criar uma nova conta."
        : "Entre com o email e senha cadastrados.";
    authNome.value = "";
    authEmail.value = "";
    authSenha.value = "";
    authSenha.autocomplete = "new-password";
}

function queryTransacoes() {
    const params = new URLSearchParams({ usuarioId: usuario.usuarioId });

    if (filtroBusca.value.trim()) {
        params.set("busca", filtroBusca.value.trim());
    }

    if (filtroTipo.value) {
        params.set("tipo", filtroTipo.value);
    }

    if (filtroMes.value) {
        params.set("mes", filtroMes.value);
    }

    return params.toString();
}

function transacoesFiltradas() {
    return transacoesAtuais.filter((transacao) => {
        const valorTransacao = Number(transacao.valor);
        const busca = filtroBusca.value.trim().toLowerCase();
        const categoriaFiltro = filtroCategoria.value;
        const inicio = filtroDataInicio.value;
        const fim = filtroDataFim.value;
        const minimo = filtroValorMin.value ? Number(filtroValorMin.value) : null;
        const maximo = filtroValorMax.value ? Number(filtroValorMax.value) : null;

        const bateBusca = !busca
            || transacao.descricao.toLowerCase().includes(busca)
            || transacao.categoria.toLowerCase().includes(busca);
        const bateCategoria = !categoriaFiltro || transacao.categoria === categoriaFiltro;
        const bateInicio = !inicio || transacao.data >= inicio;
        const bateFim = !fim || transacao.data <= fim;
        const bateMinimo = minimo === null || valorTransacao >= minimo;
        const bateMaximo = maximo === null || valorTransacao <= maximo;

        return bateBusca && bateCategoria && bateInicio && bateFim && bateMinimo && bateMaximo;
    });
}

function atualizarOpcoesCategorias(transacoes) {
    const categorias = [...new Set(transacoes.map((transacao) => transacao.categoria))].sort();
    const selecionada = filtroCategoria.value;
    filtroCategoria.innerHTML = '<option value="">Todas categorias</option>';
    document.querySelector("#listaCategoriasDatalist").innerHTML = "";

    categorias.forEach((item) => {
        const optionFiltro = document.createElement("option");
        optionFiltro.value = item;
        optionFiltro.textContent = item;
        filtroCategoria.appendChild(optionFiltro);

        const optionDatalist = document.createElement("option");
        optionDatalist.value = item;
        document.querySelector("#listaCategoriasDatalist").appendChild(optionDatalist);
    });

    if (categorias.includes(selecionada)) {
        filtroCategoria.value = selecionada;
    }
}

function atualizarResumo(transacoes) {
    const receitas = transacoes
        .filter((transacao) => transacao.tipo === "RECEITA")
        .reduce((total, transacao) => total + Number(transacao.valor), 0);
    const despesas = transacoes
        .filter((transacao) => transacao.tipo === "DESPESA")
        .reduce((total, transacao) => total + Number(transacao.valor), 0);
    const saldo = receitas - despesas;

    document.querySelector("#saldo").textContent = formatoMoeda.format(saldo);
    document.querySelector("#receitas").textContent = formatoMoeda.format(receitas);
    document.querySelector("#despesas").textContent = formatoMoeda.format(despesas);
    document.querySelector("#quantidade").textContent = transacoes.length;
    emptyState.classList.toggle("show", transacoesAtuais.length === 0);
}

function statusCalculado(transacao) {
    if (transacao.status === "PENDENTE" && transacao.vencimento && transacao.vencimento < hoje()) {
        return "ATRASADA";
    }

    return transacao.status || "PAGA";
}

async function carregarTransacoes() {
    if (!usuarioLogado()) {
        atualizarPerfil();
        return;
    }

    setLoading(true);
    try {
        const resposta = await apiFetch(`${api.transacoes}?${queryTransacoes()}`);

        if (!resposta.ok) {
            throw new Error("Erro ao carregar transacoes.");
        }

        transacoesAtuais = await resposta.json();
        renderizarTudo();
    } catch (erro) {
        mostrarMensagem("N&atilde;o foi poss&iacute;vel carregar as transa&ccedil;&otilde;es.", "error");
    } finally {
        setLoading(false);
    }
}

function renderizarTudo() {
    atualizarOpcoesCategorias(transacoesAtuais);
    const transacoes = transacoesFiltradas();
    atualizarResumo(transacoes);
    renderizarTabela(transacoes);
    renderizarCategorias(transacoes);
    renderizarGraficoMensal(transacoes);
    renderizarInsights(transacoes);
    renderizarRelatorios(transacoes);
    renderizarAlertas(transacoes);
    document.querySelector("#apiStatus").textContent = "API online";
}

function renderizarTabela(transacoes) {
    listaTransacoes.innerHTML = "";

    if (transacoes.length === 0) {
        listaTransacoes.innerHTML = '<tr><td colspan="9" class="empty">Nenhuma transa&ccedil;&atilde;o encontrada.</td></tr>';
        return;
    }

    transacoes.forEach((transacao) => {
        const receita = transacao.tipo === "RECEITA";
        const statusAtual = statusCalculado(transacao);
        const parcela = transacao.totalParcelas ? `${transacao.parcelaAtual}/${transacao.totalParcelas}` : "-";
        const linha = document.createElement("tr");
        linha.dataset.id = transacao.id;
        linha.innerHTML = `
            <td>${formatarData(transacao.data)}</td>
            <td>${escapeHtml(transacao.descricao)}</td>
            <td><span class="category-chip">${escapeHtml(abreviar(transacao.categoria))}</span>${escapeHtml(transacao.categoria)}</td>
            <td><span class="tag ${transacao.tipo}">${receita ? "Receita" : "Despesa"}</span></td>
            <td><span class="tag status-${statusAtual}">${textoStatus(statusAtual)}</span></td>
            <td>${transacao.vencimento ? formatarData(transacao.vencimento) : "-"}</td>
            <td>${parcela}${transacao.cartaoCredito ? " - Cartao" : ""}</td>
            <td class="${receita ? "money-income" : "money-expense"}">${receita ? "" : "- "}${formatoMoeda.format(transacao.valor)}</td>
            <td>
                <div class="row-actions">
                    ${statusAtual !== "PAGA" ? '<button type="button" class="success" data-action="pagar">Pagar</button>' : ""}
                    <button type="button" class="secondary" data-action="editar">Editar</button>
                    <button type="button" class="danger" data-action="excluir">Excluir</button>
                </div>
            </td>
        `;
        listaTransacoes.appendChild(linha);
    });
}

function textoStatus(valor) {
    return {
        PAGA: "Paga",
        PENDENTE: "Pendente",
        ATRASADA: "Atrasada"
    }[valor] || "Paga";
}

function abreviar(nome) {
    return nome.trim().charAt(0).toUpperCase() || "C";
}

function renderizarCategorias(transacoes) {
    const cores = ["#2979ff", "#21b99a", "#ffc233", "#ff7043", "#8b5cf6"];
    const despesas = transacoes.filter((transacao) => transacao.tipo === "DESPESA");
    const totais = {};

    despesas.forEach((transacao) => {
        totais[transacao.categoria] = (totais[transacao.categoria] || 0) + Number(transacao.valor);
    });

    const itens = Object.entries(totais).sort((a, b) => b[1] - a[1]);
    const total = itens.reduce((soma, item) => soma + item[1], 0);
    categoriaLista.innerHTML = "";

    if (itens.length === 0) {
        donutChart.style.background = "#edf2f4";
        donutChart.innerHTML = '<div class="donut-center"><strong>R$ 0,00</strong><small>Total</small></div>';
        categoriaLista.innerHTML = '<p class="empty">Nenhuma despesa no filtro.</p>';
        return;
    }

    let acumulado = 0;
    const partes = itens.map(([, itemValor], index) => {
        const inicio = (acumulado / total) * 100;
        acumulado += itemValor;
        const fim = (acumulado / total) * 100;
        return `${cores[index % cores.length]} ${inicio}% ${fim}%`;
    });

    donutChart.style.background = `conic-gradient(${partes.join(", ")})`;
    donutChart.innerHTML = `<div class="donut-center"><strong>${formatoMoeda.format(total)}</strong><small>Total</small></div>`;

    itens.slice(0, 5).forEach(([nome, itemValor], index) => {
        const percentual = total === 0 ? 0 : (itemValor / total) * 100;
        const item = document.createElement("div");
        item.className = "category-item";
        item.innerHTML = `
            <span><i class="dot" style="background:${cores[index % cores.length]}"></i>${escapeHtml(nome)}</span>
            <strong>${formatoMoeda.format(itemValor)}</strong>
            <small>${percentual.toFixed(1)}%</small>
        `;
        categoriaLista.appendChild(item);
    });
}

function renderizarGraficoMensal(transacoes) {
    const meses = ["Jun/24", "Jul/24", "Ago/24", "Set/24", "Out/24", "Nov/24", "Dez/24", "Jan/25", "Fev/25", "Mar/25", "Abr/25", "Mai/25"];
    const receitas = [8500, 9400, 8700, 8900, 12000, 13800, 10800, 11800, 12600, 10100, 12400, 12800];
    const despesas = [3900, 4200, 4600, 3600, 6200, 4100, 4050, 4900, 3900, 4500, 5100, 5400];

    transacoes.forEach((transacao) => {
        const dataTransacao = new Date(`${transacao.data}T00:00:00`);
        const index = dataTransacao.getMonth() === 4 ? 11 : dataTransacao.getMonth() % meses.length;

        if (transacao.tipo === "RECEITA") {
            receitas[index] += Number(transacao.valor);
        } else {
            despesas[index] += Number(transacao.valor);
        }
    });

    if (!window.Chart) {
        return;
    }

    if (graficoMensalChart) {
        graficoMensalChart.destroy();
    }

    graficoMensalChart = new Chart(graficoMensal, {
        type: "line",
        data: {
            labels: meses,
            datasets: [
                {
                    label: "Receitas",
                    data: receitas,
                    borderColor: "#20f3d5",
                    backgroundColor: "rgba(32, 243, 213, 0.16)",
                    tension: 0.38,
                    fill: true
                },
                {
                    label: "Despesas",
                    data: despesas,
                    borderColor: "#ff6f91",
                    backgroundColor: "rgba(255, 111, 145, 0.14)",
                    tension: 0.38,
                    fill: true
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: {
                        color: "#c7d6ea"
                    }
                }
            },
            scales: {
                x: {
                    ticks: { color: "#c7d6ea" },
                    grid: { color: "rgba(199, 214, 234, 0.10)" }
                },
                y: {
                    ticks: {
                        color: "#c7d6ea",
                        callback: (valor) => formatoMoeda.format(valor)
                    },
                    grid: { color: "rgba(199, 214, 234, 0.10)" }
                }
            }
        }
    });
}

function renderizarInsights(transacoes) {
    const receitas = transacoes
        .filter((transacao) => transacao.tipo === "RECEITA")
        .reduce((total, transacao) => total + Number(transacao.valor), 0);
    const despesas = transacoes.filter((transacao) => transacao.tipo === "DESPESA");
    const totalDespesas = despesas.reduce((total, transacao) => total + Number(transacao.valor), 0);
    const maior = despesas.reduce((atual, transacao) => {
        if (!atual || Number(transacao.valor) > Number(atual.valor)) {
            return transacao;
        }
        return atual;
    }, null);
    const media = despesas.length ? totalDespesas / despesas.length : 0;
    const saldo = receitas - totalDespesas;
    const meta = Number(metaMensal.value || 2000);
    const percentual = Math.min((totalDespesas / meta) * 100, 100);

    maiorDespesa.textContent = maior ? formatoMoeda.format(maior.valor) : "R$ 0,00";
    maiorDespesaTexto.textContent = maior ? `${maior.descricao} em ${maior.categoria}` : "Nenhuma despesa encontrada.";
    mediaGasto.textContent = formatoMoeda.format(media);
    resultadoStatus.textContent = saldo >= 0 ? "Saudavel" : "Atencao";
    resultadoTexto.textContent = saldo >= 0
        ? `Voce esta positivo em ${formatoMoeda.format(saldo)}.`
        : `Voce esta negativo em ${formatoMoeda.format(Math.abs(saldo))}.`;
    orcamentoUsado.textContent = `${Math.round(percentual)}%`;
    orcamentoTexto.textContent = `${formatoMoeda.format(totalDespesas)} usados de ${formatoMoeda.format(meta)}.`;
    orcamentoBarra.style.width = `${percentual}%`;
    dicaTexto.innerHTML = saldo >= 0
        ? "Bom resultado: mantenha as despesas abaixo das receitas e acompanhe as maiores categorias."
        : "Aten&ccedil;&atilde;o: suas despesas passaram das receitas no filtro atual.";
}

function renderizarRelatorios(transacoes) {
    const receitas = transacoes
        .filter((transacao) => transacao.tipo === "RECEITA")
        .reduce((total, transacao) => total + Number(transacao.valor), 0);
    const despesas = transacoes
        .filter((transacao) => transacao.tipo === "DESPESA")
        .reduce((total, transacao) => total + Number(transacao.valor), 0);
    const economia = receitas - despesas;
    const dias = new Set(transacoes.map((transacao) => transacao.data)).size || 1;
    const gastoDiario = despesas / dias;
    const previsao = economia + (receitas * 0.08) - (despesas * 0.04);
    const totaisCategoria = {};

    transacoes
        .filter((transacao) => transacao.tipo === "DESPESA")
        .forEach((transacao) => {
            totaisCategoria[transacao.categoria] = (totaisCategoria[transacao.categoria] || 0) + Number(transacao.valor);
        });

    document.querySelector("#economiaPeriodo").textContent = formatoMoeda.format(economia);
    document.querySelector("#gastoDiario").textContent = formatoMoeda.format(gastoDiario);
    document.querySelector("#previsaoSaldo").textContent = formatoMoeda.format(previsao);

    const lista = Object.entries(totaisCategoria).sort((a, b) => b[1] - a[1]);
    document.querySelector("#relatorioCategorias").innerHTML = lista.length
        ? lista.map(([nome, total]) => `
            <div>
                <span>${escapeHtml(nome)}</span>
                <strong>${formatoMoeda.format(total)}</strong>
            </div>
        `).join("")
        : '<p class="empty">Nenhuma categoria com despesa no filtro atual.</p>';
}

function renderizarAlertas(transacoes) {
    const receitas = transacoes
        .filter((transacao) => transacao.tipo === "RECEITA")
        .reduce((total, transacao) => total + Number(transacao.valor), 0);
    const despesas = transacoes.filter((transacao) => transacao.tipo === "DESPESA");
    const totalDespesas = despesas.reduce((total, transacao) => total + Number(transacao.valor), 0);
    const maiorDespesaItem = despesas.reduce((maior, transacao) => {
        if (!maior || Number(transacao.valor) > Number(maior.valor)) {
            return transacao;
        }
        return maior;
    }, null);
    const alertas = [];

    const meta = Number(metaMensal.value || 2000);
    const contasPendentes = transacoes.filter((transacao) => ["PENDENTE", "ATRASADA"].includes(statusCalculado(transacao)));
    const vencendo = contasPendentes.find((transacao) => {
        if (!transacao.vencimento) {
            return false;
        }
        const hojeData = new Date(`${hoje()}T00:00:00`);
        const vencimentoData = new Date(`${transacao.vencimento}T00:00:00`);
        const dias = (vencimentoData - hojeData) / 86400000;
        return dias >= 0 && dias <= 2;
    });

    if (totalDespesas > meta * 0.8) {
        alertas.push(["Orcamento", `Voce passou de 80% da meta mensal de ${formatoMoeda.format(meta)}.`]);
    }

    if (totalDespesas > receitas && receitas > 0) {
        alertas.push(["Resultado", "As despesas estao maiores que as receitas no filtro atual."]);
    }

    if (maiorDespesaItem && Number(maiorDespesaItem.valor) >= 500) {
        alertas.push(["Despesa alta", `${escapeHtml(maiorDespesaItem.descricao)} ficou em ${formatoMoeda.format(maiorDespesaItem.valor)}.`]);
    }

    if (contasPendentes.some((transacao) => statusCalculado(transacao) === "ATRASADA")) {
        alertas.push(["Conta atrasada", "Existem despesas vencidas pendentes de pagamento."]);
    }

    if (vencendo) {
        alertas.push(["Vencimento proximo", `${escapeHtml(vencendo.descricao)} vence em ${formatarData(vencendo.vencimento)}.`]);
    }

    if (alertas.length === 0) {
        alertas.push(["Tudo certo", "Nenhum risco financeiro forte encontrado no periodo filtrado."]);
    }

    document.querySelector("#alertasLista").innerHTML = alertas.map(([titulo, texto]) => `
        <div>
            <strong>${escapeHtml(titulo)}</strong>
            <p>${texto}</p>
        </div>
    `).join("");
}

function montarPayload() {
    return {
        descricao: descricao.value.trim(),
        valor: Number(valor.value),
        data: data.value,
        categoria: categoria.value.trim(),
        tipo: tipo.value,
        vencimento: vencimento.value || null,
        status: status.value,
        cartaoCredito: cartaoCredito.checked,
        usuarioId: usuario.usuarioId
    };
}

function somarMeses(dataBase, meses) {
    const dataOriginal = new Date(`${dataBase}T00:00:00`);
    const novaData = new Date(dataOriginal);
    novaData.setMonth(novaData.getMonth() + meses);
    return novaData.toISOString().slice(0, 10);
}

function montarPayloadParcelado(parcela, totalParcelas) {
    return {
        ...montarPayload(),
        descricao: `${descricao.value.trim()} (${parcela}/${totalParcelas})`,
        data: somarMeses(data.value, parcela - 1),
        vencimento: vencimento.value ? somarMeses(vencimento.value, parcela - 1) : somarMeses(data.value, parcela - 1),
        parcelaAtual: parcela,
        totalParcelas
    };
}

function limparFormulario() {
    form.reset();
    transacaoId.value = "";
    data.value = hoje();
    vencimento.value = "";
    status.value = "PAGA";
    quantidadeParcelas.value = "12";
    modo.textContent = "Nova";
}

async function salvar(event) {
    event.preventDefault();

    const id = transacaoId.value;
    const totalParcelas = Number(quantidadeParcelas.value);
    const deveParcelar = !id && parcelado.checked && totalParcelas > 1;
    const url = id ? `${api.transacoes}/${id}` : api.transacoes;
    const metodo = id ? "PUT" : "POST";

    setLoading(true);
    try {
        if (deveParcelar) {
            const respostas = await Promise.all(Array.from({ length: totalParcelas }, (_, index) => apiFetch(api.transacoes, {
                method: "POST",
                headers: jsonHeaders(),
                body: JSON.stringify(montarPayloadParcelado(index + 1, totalParcelas))
            })));

            if (respostas.some((resposta) => !resposta.ok)) {
                throw new Error("Erro ao salvar parcelas.");
            }
        } else {
            const resposta = await apiFetch(url, {
                method: metodo,
                headers: jsonHeaders(),
                body: JSON.stringify(montarPayload())
            });

            if (!resposta.ok) {
                throw new Error("Erro ao salvar.");
            }
        }

        limparFormulario();
        form.classList.remove("open");
        mostrarMensagem(deveParcelar
            ? `${totalParcelas} parcelas salvas com sucesso.`
            : id ? "Transa&ccedil;&atilde;o atualizada com sucesso." : "Transa&ccedil;&atilde;o salva com sucesso.");
        await carregarTransacoes();
    } catch (erro) {
        mostrarMensagem("N&atilde;o foi poss&iacute;vel salvar a transa&ccedil;&atilde;o.", "error");
    } finally {
        setLoading(false);
    }
}

async function carregarDadosExemplo() {
    const exemplos = [
        { descricao: "Salario", valor: 7500, data: hoje(), categoria: "Trabalho", tipo: "RECEITA" },
        { descricao: "Freelance projeto", valor: 1250, data: hoje(), categoria: "Trabalho", tipo: "RECEITA" },
        { descricao: "Supermercado", valor: 320.45, data: hoje(), categoria: "Alimentacao", tipo: "DESPESA" },
        { descricao: "Transporte", valor: 89.90, data: hoje(), categoria: "Transporte", tipo: "DESPESA" },
        { descricao: "Internet", valor: 119.90, data: hoje(), categoria: "Moradia", tipo: "DESPESA" }
    ];

    setLoading(true);
    try {
        await Promise.all(exemplos.map((transacao) => apiFetch(api.transacoes, {
            method: "POST",
            headers: jsonHeaders(),
            body: JSON.stringify({ ...transacao, usuarioId: usuario.usuarioId })
        })));
        mostrarMensagem("Dados de exemplo carregados.");
        await carregarTransacoes();
    } catch (erro) {
        mostrarMensagem("Nao foi possivel carregar os exemplos.", "error");
    } finally {
        setLoading(false);
    }
}

function editar(transacao) {
    form.classList.add("open");
    transacaoId.value = transacao.id;
    descricao.value = transacao.descricao;
    valor.value = transacao.valor;
    data.value = transacao.data;
    vencimento.value = transacao.vencimento || "";
    categoria.value = transacao.categoria;
    tipo.value = transacao.tipo;
    status.value = statusCalculado(transacao);
    parcelado.checked = false;
    cartaoCredito.checked = Boolean(transacao.cartaoCredito);
    modo.textContent = `Editando #${transacao.id}`;
    form.scrollIntoView({ behavior: "smooth", block: "center" });
}

async function marcarComoPago(transacao) {
    setLoading(true);
    try {
        const resposta = await apiFetch(`${api.transacoes}/${transacao.id}`, {
            method: "PUT",
            headers: jsonHeaders(),
            body: JSON.stringify({ ...transacao, status: "PAGA", usuarioId: usuario.usuarioId })
        });

        if (!resposta.ok) {
            mostrarMensagem("Nao foi possivel marcar como pago.", "error");
            return;
        }

        mostrarMensagem("Conta marcada como paga.");
        await carregarTransacoes();
    } finally {
        setLoading(false);
    }
}

function excluir(id) {
    idPendenteExclusao = id;
    modalConfirmacao.classList.add("open");
    modalConfirmacao.setAttribute("aria-hidden", "false");
}

async function confirmarExclusaoPendente() {
    if (!idPendenteExclusao) {
        return;
    }

    try {
        const resposta = await apiFetch(`${api.transacoes}/${idPendenteExclusao}?usuarioId=${usuario.usuarioId}`, {
            method: "DELETE"
        });

        if (!resposta.ok) {
            throw new Error("Erro ao excluir.");
        }

        mostrarMensagem("Transa&ccedil;&atilde;o exclu&iacute;da.");
        await carregarTransacoes();
    } catch (erro) {
        mostrarMensagem("N&atilde;o foi poss&iacute;vel excluir a transa&ccedil;&atilde;o.", "error");
    } finally {
        idPendenteExclusao = null;
        modalConfirmacao.classList.remove("open");
        modalConfirmacao.setAttribute("aria-hidden", "true");
    }
}

function abrirFormulario() {
    form.classList.add("open");
    form.scrollIntoView({ behavior: "smooth", block: "center" });
}

function abrirFormularioComTipo(tipoSelecionado) {
    abrirFormulario();
    tipo.value = tipoSelecionado;
    parcelado.checked = tipoSelecionado === "DESPESA";
    status.value = tipoSelecionado === "DESPESA" ? "PENDENTE" : "PAGA";
    descricao.focus();
}

async function exportarCsv() {
    setLoading(true);
    try {
        const resposta = await apiFetch(`/relatorios/csv?${queryTransacoes()}`);

        if (!resposta.ok) {
            mostrarMensagem("Nao foi possivel exportar o CSV.", "error");
            return;
        }

        const blob = await resposta.blob();
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = "saldox-relatorio.csv";
        link.click();
        URL.revokeObjectURL(url);
        mostrarMensagem("CSV exportado pelo backend com sucesso.");
    } catch (erro) {
        mostrarMensagem(erro.message || "Nao foi possivel exportar o CSV.", "error");
    } finally {
        setLoading(false);
    }
}

function exportarJson() {
    const backup = {
        usuario,
        metaMensal: metaMensal.value,
        transacoes: transacoesFiltradas()
    };
    const blob = new Blob([JSON.stringify(backup, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "saldox-transacoes.json";
    link.click();
    URL.revokeObjectURL(url);
    mostrarMensagem("JSON exportado com sucesso.");
}

async function importarBackup(arquivo) {
    if (!arquivo) {
        return;
    }

    setLoading(true);
    try {
        const conteudo = JSON.parse(await arquivo.text());
        const transacoes = Array.isArray(conteudo) ? conteudo : conteudo.transacoes || [];

        await Promise.all(transacoes.map((transacao) => apiFetch(api.transacoes, {
            method: "POST",
            headers: jsonHeaders(),
            body: JSON.stringify({
                descricao: transacao.descricao,
                valor: transacao.valor,
                data: transacao.data,
                categoria: transacao.categoria,
                tipo: transacao.tipo,
                vencimento: transacao.vencimento || null,
                status: transacao.status || "PAGA",
                parcelaAtual: transacao.parcelaAtual || null,
                totalParcelas: transacao.totalParcelas || null,
                cartaoCredito: Boolean(transacao.cartaoCredito),
                usuarioId: usuario.usuarioId
            })
        })));

        if (conteudo.metaMensal) {
            metaMensal.value = conteudo.metaMensal;
            localStorage.setItem("fintrackMetaMensal", metaMensal.value);
        }

        mostrarMensagem("Backup importado.");
        await carregarTransacoes();
    } catch (erro) {
        mostrarMensagem("Arquivo de backup invalido.", "error");
    } finally {
        importarJsonInput.value = "";
        setLoading(false);
    }
}

function copiarResumoExecutivo() {
    const texto = [
        `Saldo: ${document.querySelector("#saldo").textContent}`,
        `Receitas: ${document.querySelector("#receitas").textContent}`,
        `Despesas: ${document.querySelector("#despesas").textContent}`,
        `Transacoes: ${document.querySelector("#quantidade").textContent}`
    ].join(" | ");

    navigator.clipboard.writeText(texto)
        .then(() => mostrarMensagem("Resumo copiado."))
        .catch(() => mostrarMensagem(texto));
}

authForm.addEventListener("submit", autenticar);
alternarAuth.addEventListener("click", alternarModoAuth);
recuperarSenhaBotao?.addEventListener("click", solicitarRecuperacaoSenha);
confirmarResetSenha?.addEventListener("click", confirmarNovaSenha);
form.addEventListener("submit", salvar);
listaTransacoes?.addEventListener("click", (event) => {
    const botao = event.target.closest("button[data-action]");
    const linha = botao?.closest("tr[data-id]");

    if (!botao || !linha) {
        return;
    }

    const transacao = transacaoPorId(linha.dataset.id);
    if (!transacao) {
        mostrarMensagem("Transacao nao encontrada na lista atual.", "error");
        return;
    }

    if (botao.dataset.action === "pagar") {
        marcarComoPago(transacao);
    }

    if (botao.dataset.action === "editar") {
        editar(transacao);
    }

    if (botao.dataset.action === "excluir") {
        excluir(transacao.id);
    }
});
document.querySelector("#cancelar").addEventListener("click", () => {
    limparFormulario();
    form.classList.remove("open");
});
document.querySelector("#abrirFormulario")?.addEventListener("click", abrirFormulario);
document.querySelector("#novaReceita")?.addEventListener("click", () => abrirFormularioComTipo("RECEITA"));
document.querySelector("#novaDespesa")?.addEventListener("click", () => abrirFormularioComTipo("DESPESA"));
document.querySelector("#exportarCsv")?.addEventListener("click", exportarCsv);
document.querySelector("#exportarJson")?.addEventListener("click", exportarJson);
document.querySelector("#importarJsonBotao")?.addEventListener("click", () => importarJsonInput.click());
importarJsonInput?.addEventListener("change", () => importarBackup(importarJsonInput.files[0]));
document.querySelector("#atalhoReceita")?.addEventListener("click", () => abrirFormularioComTipo("RECEITA"));
document.querySelector("#atalhoDespesa")?.addEventListener("click", () => abrirFormularioComTipo("DESPESA"));
document.querySelector("#primeiraReceita")?.addEventListener("click", () => abrirFormularioComTipo("RECEITA"));
document.querySelector("#primeiraDespesa")?.addEventListener("click", () => abrirFormularioComTipo("DESPESA"));
document.querySelector("#carregarExemplos")?.addEventListener("click", carregarDadosExemplo);
document.querySelector("#atalhoRelatorio")?.addEventListener("click", () => {
    document.querySelector("#relatorios")?.scrollIntoView({ behavior: "smooth", block: "center" });
});
document.querySelector("#imprimirRelatorio")?.addEventListener("click", () => window.print());
document.querySelector("#copiarResumo")?.addEventListener("click", copiarResumoExecutivo);
document.querySelector("#abrirBancoH2")?.addEventListener("click", abrirBancoH2);
document.querySelector("#abrirBancoH2Config")?.addEventListener("click", abrirBancoH2);
document.querySelector("#bancoH2Link")?.addEventListener("click", abrirBancoH2);
document.querySelector("#copiarDadosH2")?.addEventListener("click", async () => {
    try {
        await navigator.clipboard.writeText("JDBC URL: jdbc:h2:file:./data/fintrack | Usuario: sa | Senha: vazia | Visualizador: /banco-h2.html");
        mostrarMensagem("Dados do H2 copiados.");
    } catch (erro) {
        mostrarMensagem("JDBC URL: jdbc:h2:file:./data/fintrack | Usuario: sa | Visualizador: /banco-h2.html.");
    }
});
document.querySelector("#atualizar").addEventListener("click", () => {
    carregarTransacoes();
    mostrarMensagem("Dados atualizados.");
});
document.querySelector("#alternarTema")?.addEventListener("click", () => {
    const temaEscuro = !document.body.classList.contains("dark-theme");
    document.body.classList.toggle("dark-theme", temaEscuro);
    localStorage.setItem("fintrackTema", temaEscuro ? "dark" : "light");
    mostrarMensagem(temaEscuro ? "Tema escuro ativado." : "Tema claro ativado.");
});
document.querySelector("#filtrarMesAtual").addEventListener("click", () => {
    filtroMes.value = mesAtual();
    carregarTransacoes();
    mostrarMensagem("Filtro do m&ecirc;s atual aplicado.");
});
document.querySelector("#limparFiltros").addEventListener("click", () => {
    filtroBusca.value = "";
    filtroTipo.value = "";
    filtroCategoria.value = "";
    filtroMes.value = "";
    filtroDataInicio.value = "";
    filtroDataFim.value = "";
    filtroValorMin.value = "";
    filtroValorMax.value = "";
    renderizarTudo();
});
document.querySelector("#sair").addEventListener("click", () => {
    localStorage.removeItem("fintrackUsuario");
    usuario = null;
    authScreen.classList.add("open");
});

document.querySelector("#salvarPerfil")?.addEventListener("click", async () => {
    const resposta = await apiFetch(`/auth/perfil/${usuario.usuarioId}`, {
        method: "PUT",
        headers: jsonHeaders(),
        body: JSON.stringify({
            nome: perfilNomeInput.value.trim() || usuario.nome,
            email: perfilEmailInput.value.trim() || usuario.email,
            senha: perfilSenhaInput.value || null
        })
    });

    if (!resposta.ok) {
        mostrarMensagem(await mensagemErroApi(resposta, "Nao foi possivel atualizar o perfil."), "error");
        return;
    }

    usuario = await resposta.json();
    localStorage.setItem("fintrackUsuario", JSON.stringify(usuario));
    perfilSenhaInput.value = "";
    atualizarPerfil();
    mostrarMensagem("Perfil salvo no banco.");
});

avatarInput?.addEventListener("change", async () => {
    if (!avatarInput.files?.[0]) {
        return;
    }

    const formData = new FormData();
    formData.append("avatar", avatarInput.files[0]);

    try {
        const resposta = await apiFetch(`/auth/avatar/${usuario.usuarioId}`, {
            method: "POST",
            body: formData
        });

        if (!resposta.ok) {
            mostrarMensagem(await mensagemErroApi(resposta, "Nao foi possivel salvar o avatar."), "error");
            return;
        }

        usuario = await resposta.json();
        localStorage.setItem("fintrackUsuario", JSON.stringify(usuario));
        atualizarPerfil();
        mostrarMensagem("Avatar salvo no perfil.");
    } finally {
        avatarInput.value = "";
    }
});

metaMensal?.addEventListener("input", () => {
    localStorage.setItem("fintrackMetaMensal", metaMensal.value || "2000");
    renderizarTudo();
});

confirmarExclusao?.addEventListener("click", confirmarExclusaoPendente);
cancelarExclusao?.addEventListener("click", () => {
    idPendenteExclusao = null;
    modalConfirmacao.classList.remove("open");
    modalConfirmacao.setAttribute("aria-hidden", "true");
});

menuToggle?.addEventListener("click", () => {
    document.body.classList.toggle("sidebar-collapsed");
});

fecharDica?.addEventListener("click", () => {
    fecharDica.closest(".tip").style.display = "none";
});

menuLinks.forEach((link) => {
    link.addEventListener("click", () => {
        menuLinks.forEach((item) => item.classList.remove("active"));
        link.classList.add("active");
        pageTitle.textContent = link.dataset.title || "Resumo";
    });
});

[filtroBusca, filtroTipo, filtroCategoria, filtroMes, filtroDataInicio, filtroDataFim, filtroValorMin, filtroValorMax].forEach((campo) => {
    campo.addEventListener("input", () => {
        if (campo === filtroBusca || campo === filtroTipo || campo === filtroMes) {
            carregarTransacoes();
            return;
        }
        renderizarTudo();
    });
    campo.addEventListener("change", () => {
        if (campo === filtroBusca || campo === filtroTipo || campo === filtroMes) {
            carregarTransacoes();
            return;
        }
        renderizarTudo();
    });
});

aplicarTemaSalvo();
nomeLabel.style.display = "none";
data.value = hoje();
atualizarPerfil();
carregarTransacoes();
