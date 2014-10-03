package managedBean;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import dao.ClienteDAO;
import bean.Cliente;

@SessionScoped
@ManagedBean
public class ClienteManagedBean {

	private Cliente cliente = new Cliente();
	private Double saldoTotal;
	private String msg = "";

	public Double getSaldoTotal() {
		return saldoTotal;
	}
	
	public void setSaldoTotal(Double saldoTotal) {
		this.saldoTotal = saldoTotal;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String logar() {
		String resultado = "";

		ClienteDAO dao = new ClienteDAO();
		Cliente user = new Cliente();
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {
			user = dao.login(cliente);
			if (user != null){
				cliente = user;
				saldoTotal = saldoTotal();
				HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
				session.setAttribute("cliente", cliente);
				resultado = "Home";
			}
			else{
				resultado = "erro";
				msg += " Usuario inexistente.";
			}

		} catch (SQLException e) {
			resultado = "erro";
			msg += " Erro de Banco de dados.";
		}

		return resultado;
	}
	
	public String simulaLogin(){
		
		cliente.setId(1);
		cliente.setNome("Renan");
		cliente.setCpf((long)123456789);
		cliente.setAgencia(10);
		cliente.setSenha("1234");
		cliente.getContaCorrente().setConta(9876);
		cliente.getContaCorrente().setSaldo(2500.0);
		cliente.getContaPoupanca().setConta(6789);
		cliente.getContaPoupanca().setSaldo(3600.0);
		saldoTotal = saldoTotal();
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		session.setAttribute("cliente", cliente);
		
		return "Home";
	}

	
	public Double saldoTotal(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		session.setAttribute("saldototal", cliente.getContaCorrente().getSaldo()+cliente.getContaPoupanca().getSaldo());
		return cliente.getContaCorrente().getSaldo()+cliente.getContaPoupanca().getSaldo();
	}
	
	public String logout(){
		String pagina = "Login";
		cliente = null;
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return pagina;
	}


}
