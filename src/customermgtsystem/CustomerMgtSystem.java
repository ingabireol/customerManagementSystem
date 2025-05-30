/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customermgtsystem;

import dao.UserDao;
import model.User;

/**
 *
 * @author YES TECHNOLOGY LTD
 */
public class CustomerMgtSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        createUser();
    }
    public static void createUser(){
        User user = new User();
        user.setEmail("kamugisha@gmail.com");
        user.setFullName("kamugisha Aimable");
        user.setActive(true);
        user.setPassword("1234567");
        user.setSalt("qwert123[]{}///=*");
        user.setUsername("kamugisha");
        user.setRole(User.ROLE_ADMIN);
        UserDao dao = new UserDao();
        dao.createUser(user);
//        dao.createDefaultAdmin();
    }
    
}
