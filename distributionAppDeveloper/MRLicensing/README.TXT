========================
BUILD OUTPUT DESCRIPTION
========================

When you build an Java application project that has a main class, the IDE
automatically copies all of the JAR
files on the projects classpath to your projects dist/lib folder. The IDE
also adds each of the JAR files to the Class-Path element in the application
JAR files manifest file (MANIFEST.MF).

To run the project from the command line, go to the dist folder and
type the following:

java -jar "MRLicensing.jar" 

To distribute this project, zip up the dist folder (including the lib folder)
and distribute the ZIP file.

Notes:

* If two JAR files on the project classpath have the same name, only the first
JAR file is copied to the lib folder.
* Only JAR files are copied to the lib folder.
If the classpath contains other types of files or folders, these files (folders)
are not copied.
* If a library on the projects classpath also has a Class-Path element
specified in the manifest,the content of the Class-Path element has to be on
the projects runtime path.
* To set a main class in a standard Java project, right-click the project node
in the Projects window and choose Properties. Then click Run and enter the
class name in the Main Class field. Alternatively, you can manually type the
class name in the manifest Main-Class element.

            

            ============================================================================
            ## Instructions to integrate MRLicensing Library into your Application ##
            ============================================================================
            *current directory should be the distribuition of the MRLicensing
            *in addition to the current folder you must have the distribuition folder of the LicenseManager, necessary to manager User Licenses and Regist Applications
            (Advised steps, Based on NetBeans IDE and Apache Ant java compiler)

                1-In current Folder, copy MRLicensing.jar to lib Folder.

                2-Copy lib Folder to Your Project, inside src Folder ("currentDir"\lib   ---->   "YourProject"\src\lib)

                3-In your IDE, in Your Project add all libraries, using "Add JAR/Folder..." option (right click on Libraries Folder)
                *repeat step for each library (jar file)
                

                4-Copy LicenseRep Folder to Your Project ("currentDir"\LicenseRep   ---->    "YourProject"\LicenseRep)
                *LicenseRep must contain managerCertificate.crt ("YourProject"\LicenseRep\managerCertificate.crt)
    
                5-Edit "YourProject"\build.xml , add code between "Start of edit" and "End of edit" comments (following code is a simple example of build.xml):

                <?xml version="1.0" encoding="UTF-8"?>
                <project name="YourProject" default="default" basedir=".">
                    <description>Builds, tests, and runs the project YourProject.</description>
                    <import file="nbproject/build-impl.xml"/>
                    <!-- Start of edit  -->
                    <target name="-post-jar">
                        <copy todir="&#36;{dist.dir}/LicenseRep">
                            <fileset file="&#36;{basedir}/LicenseRep/managerCertificate.crt"/>
                        </copy>
                    </target>
                    <!--  End of edit -->
                </project>

                6-Make sure that you are copying the lib Folder to your Distribuition when executing the build project
                *right click Your Project, select Properties->(Categories:->)Build->Packaging, make sure that "Copy Dependent Libraries" option is selected, then Ok button

                7-How to use MRLicensing in your app code (replace: YourProject to the name of your Project and #.#.# to the version)
                
                    a) initialization:
                        In mainClass:
                        import logic.MRLicensing;
                            In start of main():
                            MRLicensing mrlicensing=new MRLicensing();
                            mrlicensing.init("YourProject", "#.#.#");

                    b) make your code, license dependent, using ".isRegistered()", example:
                        if(mrlicensing.isRegistered()){
                            #your code here
                        }
                        else{
                            #inform missing valid License
                            #or do step c)  
                        }

                    c) Implement Requirement, request new license, using ".startRegistration()":
                        if(mrlicensing.startRegistration()){
                            #sucess-->License Request generated in LicenseRep Folder
                        }

                8- Regist your Application in the License Manager (for info: java -jar "LicenseManager.jar" help):
                    a) After Ending the development of the code to be distibuited, clean and build.

                    b) run command (replace: YourProject to the name of your Project, #.#.# to the version and path.of.project.jar to the Your Project .jar file Path):
                        java -jar "LicenseManager.jar" add-app YourProject #.#.# "path.of.project.jar"

            ============================================================================
                    ##      End of Instructions about MRLicensing Library   ##
            ============================================================================ 

            ============================================================================
                        ##      Instructions to Manage User Licenses      ##
            ============================================================================
            *To use LicenseManager, should be in distribuition folder of LicenseManager and run appropriate commands
            *To see available commands and details, run : java -jar "LicenseManager.jar" help

            When a user wants to use your application, he must purchase the application directly to you and provide the following data:
                -which registered application he wants to purchase
                -a email to be associated with the User
                -the Portuguese Citizen Card to be inserted in the Smart Card Reader
            Then use the command(and follow the intructions): java -jar "LicenseManager.jar" purchase 


            When a user is asking for a License, he must provide the License Request generated by the MRLicensing library integrated by a distribuition of your application
            License Request must me named like this: MRLicReq_"email@of.user".zip
            Steps to generate User License:
                Choose one method:
                a) Copy the License Request to the folder LicenseRep (delete any other License Request in LicenseRep folder) and run command:
                    java -jar "LicenseManager.jar" generate-license [validity]
                    *validity-> validity of generated License in dd/MM/yyyy

                or
                
                b) run command (replace: path.to.new.license.request.zip to the License Request path in the computer):
                    java -jar "LicenseManager.jar" generate-license [validity] "path.to.new.license.request.zip"
                    *validity-> validity of generated License in dd/MM/yyyy


            Then send to the User the License (MRLic_"email@of.user".zip) generated at LicenseRep/"gameName"/"email@of.user"/

            ============================================================================
                    ##    End of Instructions about Manage User Licenses     ##
            ============================================================================
            
        