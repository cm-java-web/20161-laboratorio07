package br.javaweb.aj.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import br.javaweb.beans.Produto;
import br.javaweb.util.JavaWebException;

public class ProdutoDAOImpl implements ProdutoDAO {

    private final String INSERT_QUERY = "insert into produtos (nome,codigo,preco,descricao,image) values (?,?,?,?,?)";
    private final String SELECT_ALL_QUERY = "select * from produtos";
    private final String SELECT_BY_ID_QUERY = "select * from produtos where id = ? ";
    
    //private final static String CREATE_TABLE_MYSQL = "CREATE TABLE IF NOT EXISTS produtos ( nome varchar(50) default NULL, codigo varchar(50) default NULL,  preco float(50,0) default NULL,`descricao` varchar(50) default NULL,image varchar(50) default NULL,id smallint NOT NULL auto_increment,PRIMARY KEY  (id))";
    private final static String CREATE_TABLE_DERBY = "CREATE TABLE produtos ( nome varchar(50) default NULL, codigo varchar(50) default NULL,  preco float(50,0) default NULL,descricao varchar(50) default NULL,image varchar(50) default NULL,id smallint NOT NULL GENERATED ALWAYS AS IDENTITY,PRIMARY KEY  (id))";

    public void save(Produto p) throws JavaWebException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement prepStmt = null;
        try {
            conn = GerenciadorConexoes.getConexao();
            prepStmt = conn.prepareStatement(INSERT_QUERY);
            prepStmt.setString(1, p.getNome());
            prepStmt.setString(2, p.getCodigo());
            prepStmt.setDouble(3, p.getPreco());
            prepStmt.setString(4, p.getDescricao());
            prepStmt.setString(5, p.getImage());
            prepStmt.execute();
        } catch (SQLException e) {
            String msg = "[ProdutosDB][save(Produto p)]: " + e.getMessage();
            JavaWebException ge = new JavaWebException(msg, e);
            throw ge;
        } finally {
            GerenciadorConexoes.closeAll(conn, prepStmt, rs);
        }
    }

    public List<Produto> getCatalogoProdutos() throws JavaWebException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Produto> listaProdutos = new ArrayList<Produto>();
        try {
            conn = GerenciadorConexoes.getConexao();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SELECT_ALL_QUERY);
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String codigo = rs.getString("codigo");
                String descricao = rs.getString("descricao");
                String imagem = rs.getString("image");
                double preco = rs.getFloat("preco");
                Produto p = new Produto(id, nome, codigo, descricao, preco, imagem);
                listaProdutos.add(p);
            }
        } catch (SQLException e) {
            String msg = "[ProdutosDB][getCatalogoProdutos()]: " + e.getMessage();
            JavaWebException ge = new JavaWebException(msg, e);
            throw ge;
        } finally {
            GerenciadorConexoes.closeAll(conn, stmt, rs);
        }
        return listaProdutos;
    }

    public Produto getProdutoById(int id) throws JavaWebException {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Produto oProduto = null;
        try {
            conn = GerenciadorConexoes.getConexao();
            prepStmt = conn.prepareStatement(SELECT_BY_ID_QUERY);
            prepStmt.setInt(1, id);
            rs = prepStmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                String codigo = rs.getString("codigo");
                String descricao = rs.getString("descricao");
                String imagem = rs.getString("image");
                double preco = rs.getFloat("preco");
                oProduto = new Produto(id, nome, codigo, descricao, preco, imagem);
            }
        } catch (SQLException e) {
            String msg = "[ProdutosDB][getProdutoById()]: " + e.getMessage();
            JavaWebException ge = new JavaWebException(msg, e);
            throw ge;
        } finally {
            GerenciadorConexoes.closeAll(conn, prepStmt, rs);
        }
        return oProduto;
    }

    public void createTable() throws JavaWebException {
        Connection conn = null;
        Statement stmt = null;
        try {

            conn = GerenciadorConexoes.getConexao();
            stmt = conn.createStatement();
            stmt.executeUpdate(CREATE_TABLE_DERBY);

            Produto[] produtos = {
                new Produto( "Maquina fotografica", "maqFot001", "Maquina fotografica", 80, "maqFot001.gif"),
                new Produto( "CD - Meu Reino Encantado", "cd003", "Daniel", 18, "cd003.gif"),
                new Produto( "TV 29", "tv29philips", "29' Tela Plana", 1750, "tv001.gif"),
                new Produto( "CD - As Gargantas do Brasil", "cd001", "Milionario e Jose Rico", 13, "cd001.gif"),
                new Produto( "CD - Ta Nervoso...Vai Pesca", "cd002", "Ataide & Alexandre", 15, "cd002.gif")
            };

            String insertComId = "insert into produtos (nome,codigo,preco,descricao,image) values (?,?,?,?,?)";
            PreparedStatement prepStmt = conn.prepareStatement(insertComId);
            for (int i = 0; i < produtos.length; i++) {
                Produto p = produtos[i];
                prepStmt.setString(1, p.getNome());
                prepStmt.setString(2, p.getCodigo());
                prepStmt.setDouble(3, p.getPreco());
                prepStmt.setString(4, p.getDescricao());
                prepStmt.setString(5, p.getImage());
                prepStmt.execute();
                prepStmt.clearParameters();
            }
            prepStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new JavaWebException("Erro ao criar a tabela de produtos", e);
        } finally {
            GerenciadorConexoes.closeAll(conn, stmt);
        }
    }
}
