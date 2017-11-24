//
//  LoginViewController.swift
//  ColonelFund
//
//  Created by Chris Penner on 11/21/17.
//  Copyright © 2017 PennerTech. All rights reserved.
//

import UIKit
import FBSDKCoreKit
import FacebookCore
import FacebookLogin
import FBSDKLoginKit

class LoginViewController: UIViewController {
    
    //MARK: - Properties
    @IBOutlet weak var usernameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var fbLoginButton: FBSDKLoginButton!
    
    var username = "admin"
    var password = "admin"
    
    @IBAction func loginButtonPressed(_ sender: Any) {
        if (usernameTextField.text == username && passwordTextField.text == password) {
            print("Successfully logged in as: \(usernameTextField.text!)")
            performSegue(withIdentifier: "ShowMain", sender: self)
        } else {
            print("Invalid username and password combination")
        }
    }
    
    @IBAction func fbLoginButtonPressed(_ sender: Any) {
        //Test ID: testy_ckbaqld_mctesterson@tfbnw.net
        //Test Pass: testp@ss
        let loginManager = LoginManager()
        loginManager.logIn(readPermissions: [ .publicProfile, .email ], viewController: self) { loginResult in
            switch loginResult {
            case .failed(let error):
                print(error)
            case .cancelled:
                print("User cancelled login.")
            case .success(let grantedPermissions, let declinedPermissions, let accessToken):
                self.getFBUserData()
            }
        }
    }
    
    
    var dict : [String : AnyObject]!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        configureFBLoginButton()
    }
    
    func configureFBLoginButton() {
        //Check if logged in
        if let accessToken = FBSDKAccessToken.current(){
            getFBUserData()
        }
    }
    
    //Get user data
    func getFBUserData(){
        if((FBSDKAccessToken.current()) != nil){
            FBSDKGraphRequest(graphPath: "me", parameters: ["fields": "id, name, picture.type(large), email"]).start(completionHandler: { (connection, result, error) -> Void in
                if (error == nil){
                    self.dict = result as! [String : AnyObject]
                    print(result!)
                    print(self.dict)
                    self.performSegue(withIdentifier: "ShowMain", sender: self)
                }
            })
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
//    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        super.prepare(for: segue, sender: sender)
//        switch(segue.identifier ?? "") {
//        case "ShowMain":
//            guard let mainViewController = segue.destination as? MainViewController else {
//                fatalError("Unexpected destination: \(segue.destination)")
//            }
//
//            guard let loginButton = sender as? EventListTableViewCell else {
//                fatalError("Unexpected sender: \(sender)")
//            }
//
//
//        default:
//            fatalError("Unexpected Segue Identifier: \(segue.identifier)")
//        }
//    }
 
    
}