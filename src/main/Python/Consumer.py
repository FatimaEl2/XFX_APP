from tkinter import *
import requests
import os


base_url = "http://localhost:8080/"

def browse():
    
    endpoint = "browse"
    path = input("Enter the path to the folder you want to browse leave it if you want to browse the root: ")
    url = f"{base_url}{endpoint}?path={path}"
    response = requests.get(url)
   

    if response.status_code == 200:
        files = response.json()
        for file in files:
            print(f"Name: {file['name']}, Type: {file['type']}, Size: {file['size']}")
    elif response.status_code == 404:
        print(" the directory is not found.")
    elif response.status_code == 400:
        print("Invalid request.")
    else:
        print("An error occurred while browsing the directory.")


def rename():
    endpoint = "rename"
    filename = input("Enter the file name or the directory name you want to change: ")
    newname = input("Enter the new name for this file or folder: ")
    url = f"{base_url}{endpoint}?path={filename}&newname={newname}"
    response = requests.patch(url)
    



def download():
    endpoint = "download"
    filename = input("Enter the file name you want to download: ")
    path = input("Enter the path where the file exists, or leave it empty if it is in the root directory: ")
    url = f"{base_url}{endpoint}?filename={filename}&path={path}"
  
    response = requests.get(url)
   
    if response.status_code == 200:
        content_disposition = response.headers.get("content-disposition")
        if content_disposition:
            filename = content_disposition.split("filename=")[-1]
            filename = filename.strip('"')

        download_path = os.path.join("C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\src\\main\\Python\\ClientDirectory", filename)

        with open(download_path, "wb") as f:
            f.write(response.content)

        print("Success, the file is downloaded to:", download_path)
    else:
        print("Failure, we could not download the file. Status code:", response.status_code)


def upload():
    url = "http://localhost:8080/upload"
    filename = input("Enter the file name you want to upload: ")
    filepath = "C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\src\\main\\Python\\ClientDirectory\\"+filename
    with open(filepath, "rb") as f:
        files = {'File': (filename, f.read())}
        response = requests.post(url, files=files)
        # Update the status message
        print(response.content)
           

def delete():

    endpoint = "delete"
    path = input("Enter the name of the file you want to delete, if it is in a subfolder please specify it: ")
    url = f"{base_url}{endpoint}?path={path}"
    response = requests.delete(url)

    if response.status_code == 200:
        print("Success, the file is deleted.")
    elif response.status_code == 400:
        print(f"Failure, we could not delete the file. {response.text}")
    else:
        print("An error occurred while deleting the file.")



def menu():
    while True:
        print("Welcome to XFX Menu!")
        
        print("1# Browse a directory")
        print("2# Rename file/directory")
        print("3# Download file")
        print("4# Upload file")
        print("5# Delete file")
        print("6# Quit")
        choice = input("Select an option (1, 2, 3, 4, 5, 6 to quit): ")

        if choice == "6":
            print("Goodbye!")
            break 

        if choice == "1":
            browse()
        elif choice == "2":
            rename()
        elif choice == "3":
            download()
        elif choice == "4":
            upload()
        elif choice == "5":
            delete()
            
        else:
            print("Invalid choice. Please select a valid option.")


menu()




