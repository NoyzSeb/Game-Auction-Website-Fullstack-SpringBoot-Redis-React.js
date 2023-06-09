package com.service;


import java.util.List;
import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.model.UserModel;
import com.repo.UserRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public boolean userExist;
    public UserModel searchedUser;
    
    public UserService (UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public List<UserModel> getAllUsers(){
        return (List<UserModel>) userRepo.findAll();
    }

    public UserModel getUserById(Long id){
        

        return userRepo.findById(id).orElseThrow(()-> new RuntimeException("Couldnt find the user with"+id));
    }
    
    //These for loops create manually because "CrudRepository"'s custom method doesn't work or doesnt exist in latest version.
    /*********/
    public boolean existByName(String name){
        for(int i=0; i<getAllUsers().size();i++){
            if(getAllUsers().get(i).getName().equals(name)){
               System.out.println("Input " + name + " Data "+ getAllUsers().get(i) );
               userExist=true;
            }
        }
        return userExist;
    }
    public UserModel getUserByName(String name){
        for(int i=0; i<getAllUsers().size();i++){
            if(getAllUsers().get(i).getName().equals(name)){
               System.out.println("Input " + name + " Data "+ getAllUsers().get(i) );
               searchedUser = getAllUsers().get(i);
            }
        }

        if(searchedUser !=null){
            return searchedUser;
        }else{
            throw new EntityNotFoundException("There is no User with this name.");
        }
      
       
    }
    /*********/


    public UserModel createUser(UserModel user){
        
        if(existByName(user.getName())){
            throw new EntityExistsException("There is an account is already exist with "+ user.getName());
        }else{
            if(user.getName().equals("admin")&&user.getPassword().equals("admin")){
                user.setRole("ADMIN");
                return userRepo.save(user);
            }else{
                user.setRole("USER");
                return userRepo.save(user);

            }
        }
    }
    
    public UserModel updateUser(UserModel user, Long id){
        UserModel oldUser= getUserById(id);

        if(user.getName()!=null)oldUser.setName(user.getName());
        if(user.getPassword()!=null)oldUser.setPassword(user.getPassword());
        if(user.isLogged()!=true)oldUser.setLogged(user.isLogged());
        if(user.getRole()!=null)oldUser.setRole(user.getRole());

        return userRepo.save(oldUser);
    }

    public UserModel loggedUser(UserModel user) throws InvalidAttributeValueException{
       UserModel logging_user = getUserByName(user.getName());
        

       try {             
             if(logging_user.getPassword().equals(user.getPassword())&&logging_user.getName().equals(user.getName())){
                  logging_user.setLogged(true);
                  userRepo.save(logging_user);
                  return logging_user;
              }else{
                  throw new InvalidAttributeValueException("Invalid Login");
              }
       } catch (Exception e) {
           System.out.println(e.getClass() + " " + e.getCause());
           userRepo.save(logging_user);
           
           return user;
           
       } 
        
    }
    
    public void deleteUser(Long id){
       userRepo.deleteById(id);
    }
}
