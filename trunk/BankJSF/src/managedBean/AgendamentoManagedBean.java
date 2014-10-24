package managedBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.AgendamentoDAO;
import dao.ClienteDAO;
import bean.Agendamento;
import bean.Cliente;


@SessionScoped
@ManagedBean
public class AgendamentoManagedBean {
	
	private Agendamento a = new Agendamento();
	private Cliente usuario = new Cliente();
	private List<Agendamento> listaAgenda = new ArrayList<Agendamento>();
	private int senhaCartao;
	private String msg;
	
	public Agendamento getA() {
		return a;
	}
	public void setA(Agendamento a) {
		this.a = a;
	}
	public List<Agendamento> getListaAgenda() {
		return listaAgenda;
	}
	public void setListaAgenda(List<Agendamento> listaAgenda) {
		this.listaAgenda = listaAgenda;
	}
	public int getSenhaCartao() {
		return senhaCartao;
	}
	public void setSenhaCartao(int senhaCartao) {
		this.senhaCartao = senhaCartao;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
		
	public String agendar(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		usuario = (Cliente) session.getAttribute("cliente");
		
		if(usuario.getSenhaCartao() == senhaCartao){	
		
			AgendamentoDAO dao = new AgendamentoDAO();
			
			a.setIdUsuario(usuario.getId());
			
			try {			
				dao.setarAgendamento(a);					
				pagina = "sucesso";			
			} catch (SQLException e) {
				pagina = "erro";
				msg = e.getMessage();
			}
		}else{
			pagina = "erro";
			msg = "Senha incorreta";
		}
		
		return pagina + ".faces?faces-redirect=true";
	}
	
	public String alterar(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		usuario = (Cliente) session.getAttribute("cliente");
		
		AgendamentoDAO dao = new AgendamentoDAO();
		
		a.setIdUsuario(usuario.getId());	
		
		try{			
			if(a.getContaD()!= null && a.getAgenciaD() != null){ //S� seta ID quando j� tiver Ag e CC
				ClienteDAO tempDAO = new ClienteDAO();
				Cliente c= tempDAO.getCliente(a.getContaD(), a.getAgenciaD());
				a.setIdD(c.getId());
			}
			
			dao.alterarAgendamento(a);
			pagina = "sucesso";
		}catch(SQLException e){
			pagina = "erro";
			msg = e.getMessage();
		}
		
		return pagina + ".faces?faces-redirect=true";
	}
	
	public String listarAgenda(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		usuario = (Cliente) session.getAttribute("cliente");
			
		AgendamentoDAO dao = new AgendamentoDAO();
		
		try {
			listaAgenda = dao.pegarAgenda(usuario.getId());
			pagina = "listarAgenda";
		} catch (SQLException e) {
			pagina = "erro";
			msg += e;
		}
		
		return pagina + ".faces?faces-redirect=true";
	}
	
	public String excluir(){
		String pagina = "";
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		usuario = (Cliente) session.getAttribute("cliente");
		
		AgendamentoDAO dao = new AgendamentoDAO();
		
		try{
			dao.excluirAgendamento(usuario.getId());
			pagina = "listarAgenda";
		}catch(SQLException e){
			pagina="erro";
			msg = e.getMessage();
		}
		
		return pagina + ".faces?faces-redirect=true";
	}
	
	

}