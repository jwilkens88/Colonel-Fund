<?php

$response = array("error" => FALSE);

$conn = __construct();

// constructor
function __construct() {
    require_once 'android_login_connect.php';
    // connecting to database
    $db = new android_login_connect();
    $conn = $db->connect();
    return $conn;
}

$query = "SELECT userID, firstName, lastName, emailAddress, phoneNumber, username, profilePicURL, facebookID, googleID, firebaseID FROM members";
$response = mysqli_query($conn,$query) or die("Couldn't execute query.");

$rows = array();
while($r = mysqli_fetch_assoc($response)) {
    $rows[] = $r;
}

echo json_encode($rows);

mysqli_close($conn);
?>
