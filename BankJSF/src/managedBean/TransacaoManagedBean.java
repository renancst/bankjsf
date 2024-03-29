package managedBean;

import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.TransacaoDAO;
import bean.Cliente;
import bean.Favorito;
import bean.Transacao;

@SessionScoped
@ManagedBean
public class TransacaoManagedBean {
	
	private Transacao transacao = new Transacao();
	private Cliente remetente;
	private Favorito favorito;
	private Transacao comprovante = new Transacao();
	private String senhaCartao;
	private String tipoTransacao;
	private String msg;
	private Date fromDate = new Date();
	private Date untilDate = new Date();
	private String codBarras1 = "";
	private String codBarras2 = "";
	private String codBarras3 = "";
	private String codBarras4 = "";
	
	ResourceBundle bundle = ResourceBundle.getBundle("language_" + FacesContext.getCurrentInstance().getViewRoot().getLocale());
	String senhaErrada = bundle.getString("senhaErrada");
	
	public Transacao getTransacao() {
		return transacao;
	}
	public void setTransacao(Transacao transacao) {
		this.transacao = transacao;
	}
	public Cliente getRemetente() {
		return remetente;
	}
	public Favorito getFavorito() {
		return favorito;
	}
	public void setFavorito(Favorito favorito) {
		this.favorito = favorito;
	}
	public Transacao getComprovante() {
		return comprovante;
	}
	public void setComprovante(Transacao comprovante) {
		this.comprovante = comprovante;
	}
	public void setRemetente(Cliente remetente) {
		this.remetente = remetente;
	}
	public String getSenhaCartao() {
		return senhaCartao;
	}
	public void setSenhaCartao(String senhaCartao) {
		this.senhaCartao = senhaCartao;
	}
	public String getTipoTransacao() {
		return tipoTransacao;
	}
	public void setTipoTransacao(String tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getUntilDate() {
		return untilDate;
	}
	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}
	public String getCodBarras1() {
		return codBarras1;
	}
	public void setCodBarras1(String codBarras1) {
		this.codBarras1 = codBarras1;
	}
	public String getCodBarras2() {
		return codBarras2;
	}
	public void setCodBarras2(String codBarras2) {
		this.codBarras2 = codBarras2;
	}
	public String getCodBarras3() {
		return codBarras3;
	}
	public void setCodBarras3(String codBarras3) {
		this.codBarras3 = codBarras3;
	}
	public String getCodBarras4() {
		return codBarras4;
	}
	public void setCodBarras4(String codBarras4) {
		this.codBarras4 = codBarras4;
	}
	
		
	public String transacao(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		remetente = (Cliente) session.getAttribute("cliente");
		
		if(remetente == null)
			return "Login" + ".faces?faces-redirect=true";
		
		if(!remetente.getSenhaCartao().equals(senhaCartao)){
			setMsg(senhaErrada);
			return "erro" + ".faces?faces-redirect=true";			
		}
		
		if(tipoTransacao.equals("transfToPoupanca"))
			pagina = transfToPoupanca(pagina, session);				
		else 
		if(tipoTransacao.equals("transfToCc"))
			pagina = transfToCc(pagina, session);		
		else
		if(tipoTransacao.equals("transfToTerc"))
			pagina = transfToTerc(pagina, session);
		else
		if (tipoTransacao.equals("Payment"))
			pagina = Payment(pagina, session);
		
		transacao = new Transacao();
		return pagina + ".faces?faces-redirect=true";	
	}
	
	
	
	public String transfToPoupanca(String pagina, HttpSession session){
		
		remetente.getContaCorrente().setSaldo(remetente.getContaCorrente().getSaldo() - transacao.getValor());
		remetente.getContaPoupanca().setSaldo(remetente.getContaPoupanca().getSaldo() + transacao.getValor());
		
		//Setar tudo pro proprio cliente - Preparar grava��o de Historico
		transacao.setIdD(remetente.getId());
		transacao.setAgenciaD(remetente.getAgencia());
		transacao.setContaD(remetente.getContaPoupanca().getConta());
		transacao.setIdR(remetente.getId());
		transacao.setAgenciaR(remetente.getAgencia());
		transacao.setContaR(remetente.getContaCorrente().getConta());
		transacao.setData(new java.util.Date());
		transacao.setTipoTransacao(tipoTransacao);
		transacao.setSaldoR(remetente.getContaCorrente().getSaldo());
		transacao.setSaldoD(remetente.getContaPoupanca().getSaldo());
		transacao.setNomeR(remetente.getNome());
		transacao.setNomeD(remetente.getNome());
		
		if(transacao.getDescricao().equals(""))
			transacao.setDescricao("Transf. de Conta Corrente p/ Conta Poup.");
		
		TransacaoDAO dao = new TransacaoDAO();
		try {					
			remetente = dao.transfToPoupanca(remetente, transacao);
			//setListaUltimosLanc(dao.ultimosLanc(remetente.getId()));
			//setComprovantes(dao.comprovantes(remetente.getId()));
			session.setAttribute("listaUltimosLanc", dao.ultimosLanc(remetente.getId()));
			session.setAttribute("comprovantes", dao.comprovantes(remetente.getId()));
			pagina = "sucesso";			
		} catch (SQLException e) {
			pagina = "erro";
		}
		
		session.setAttribute("cliente", remetente);	
		
		return pagina;
	}			
		
	public String transfToCc(String pagina, HttpSession session){
		
		remetente.getContaCorrente().setSaldo(remetente.getContaCorrente().getSaldo() + transacao.getValor());
		remetente.getContaPoupanca().setSaldo(remetente.getContaPoupanca().getSaldo() - transacao.getValor());
		
		//Setar tudo pro proprio cliente - Preparar grava��o de Historico
		transacao.setIdD(remetente.getId());
		transacao.setAgenciaD(remetente.getAgencia());
		transacao.setContaD(remetente.getContaCorrente().getConta());
		transacao.setIdR(remetente.getId());
		transacao.setAgenciaR(remetente.getAgencia());
		transacao.setContaR(remetente.getContaPoupanca().getConta());
		transacao.setData(new java.util.Date());
		transacao.setTipoTransacao(tipoTransacao);
		transacao.setSaldoR(remetente.getContaPoupanca().getSaldo());
		transacao.setSaldoD(remetente.getContaCorrente().getSaldo());
		transacao.setNomeR(remetente.getNome());
		transacao.setNomeD(remetente.getNome());
		
		if(transacao.getDescricao().equals(""))
			transacao.setDescricao("Transf. de Conta Poup. p/ Conta Corrente");
		
		TransacaoDAO dao = new TransacaoDAO();
		try {	
				remetente = dao.transfToCc(remetente, transacao);
				//setListaUltimosLanc(dao.ultimosLanc(remetente.getId()));
				//setComprovantes(dao.comprovantes(remetente.getId()));
				session.setAttribute("listaUltimosLanc", dao.ultimosLanc(remetente.getId()));
				session.setAttribute("comprovantes", dao.comprovantes(remetente.getId()));
				pagina = "sucesso";
		}catch (SQLException e) {
			pagina = "erro";
			setMsg("Erro de comunica��o com o banco de dados!");
		}
		
		session.setAttribute("cliente", remetente);
		
		return pagina;	
	}					
	
	public String transfToTerc(String pagina, HttpSession session){
				
		transacao.setData(new java.util.Date());
		transacao.setTipoTransacao(tipoTransacao);
		
		TransacaoDAO dao = new TransacaoDAO();
		try {
			remetente = dao.transferencia(remetente, transacao);
			//setListaUltimosLanc(dao.ultimosLanc(remetente.getId()));
			//setComprovantes(dao.comprovantes(remetente.getId()));
			session.setAttribute("listaUltimosLanc", dao.ultimosLanc(remetente.getId()));
			session.setAttribute("comprovantes", dao.comprovantes(remetente.getId()));
			pagina = "sucesso";
		} catch (SQLException e) {
			pagina = "erro";
			setMsg("SQLException!");
		} catch (NullPointerException e) {
			pagina = "erro";
			setMsg("Destinat�rio inexistente!");
		} catch(IllegalArgumentException e){
			pagina = "erro";
			setMsg("N�o foi poss�vel realizar esta transa��o!");
		}
		
		session.setAttribute("cliente", remetente);
		session.setAttribute("saldototal", remetente.getContaCorrente().getSaldo() + remetente.getContaPoupanca().getSaldo());
		
		return pagina;		
	}
	
	public String Payment(String pagina, HttpSession session){
		
		remetente.getContaCorrente().setSaldo(remetente.getContaCorrente().getSaldo() - transacao.getValor());
		
		transacao.setData(new java.util.Date());
		transacao.setTipoTransacao(tipoTransacao);
		transacao.setIdR(remetente.getId());
		transacao.setNomeR(remetente.getNome());
		transacao.setAgenciaR(remetente.getAgencia());
		transacao.setContaR(remetente.getContaCorrente().getConta());
		transacao.setSaldoR(remetente.getContaCorrente().getSaldo());	
		transacao.setCodBarras(codBarras1+codBarras2+codBarras3+codBarras4);
		if(transacao.getDescricao().equals(""))
			transacao.setDescricao("Pagamento de Conta (Final " + codBarras4 + ")");
		
		TransacaoDAO dao = new TransacaoDAO();
		try {
			remetente = dao.pagamento(remetente, transacao);
			//setListaUltimosLanc(dao.ultimosLanc(remetente.getId()));
			//setComprovantes(dao.comprovantes(remetente.getId()));
			session.setAttribute("listaUltimosLanc", dao.ultimosLanc(remetente.getId()));
			session.setAttribute("comprovantes", dao.comprovantes(remetente.getId()));
			pagina = "sucesso";
		} catch (SQLException e) {
			pagina = "erro";
			setMsg("Erro de comunica��o com o banco de dados!");
		} catch (NullPointerException e) {
			pagina = "erro";
			setMsg("Null Pointer!");
		}
		
		session.setAttribute("cliente", remetente);
		session.setAttribute("saldototal", remetente.getContaCorrente().getSaldo() + remetente.getContaPoupanca().getSaldo());
		
		return pagina;
	}
	
	
	
	
	public String selecionarFavorito(){
		String pagina = "transferenciaTerceiro";
		
		transacao.setAgenciaD(favorito.getAgencia());
		transacao.setContaD(favorito.getContaC());
		
		return pagina + ".faces?faces-redirect=true";
		
	}

	public String listarExtrato(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		remetente = (Cliente) session.getAttribute("cliente");
		
		if(remetente == null)
			return "Login" + ".faces?faces-redirect=true";
		
		TransacaoDAO dao = new TransacaoDAO();
		try {
			//setListaExtrato(dao.historico(remetente.getId(), fromDate, untilDate));
			session.setAttribute("listaExtrato", dao.historico(remetente.getId(), fromDate, untilDate));
			pagina = "Extrato";
		} catch (SQLException e) {
			pagina = "erro";
			msg += e;
		}
		
		
		return pagina + ".faces?faces-redirect=true";
	}
	
	public String listarUltimosLanc(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		remetente = (Cliente) session.getAttribute("cliente");
		
		if(remetente == null)
			return "Login" + ".faces?faces-redirect=true";
			
		TransacaoDAO dao = new TransacaoDAO();
		try {
			//setListaUltimosLanc(dao.ultimosLanc(remetente.getId()));
			session.setAttribute("listaUltimosLanc", dao.ultimosLanc(remetente.getId()));
			pagina = "Home";
		} catch (SQLException e) {
			pagina = "erro";
			msg = e.toString();
		}
		
		
		return pagina + ".faces?faces-redirect=true";
	}
	
	public String listarComprovantes(){
		
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		remetente = (Cliente) session.getAttribute("cliente");
		
		if(remetente == null)
			return "Login" + ".faces?faces-redirect=true";
		
		TransacaoDAO dao = new TransacaoDAO();
		try{
			session.setAttribute("comprovantes", dao.comprovantes(remetente.getId()));
			pagina = "Comprovantes";
		} catch (SQLException e){
			pagina = "erro";
			msg = e.toString();
		}
			
		return pagina + ".faces?faces-redirect=true";
	}
	
}
