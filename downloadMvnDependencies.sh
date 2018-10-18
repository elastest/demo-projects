for D in `find . -type d -maxdepth 1`
do
   cd ${D};
   mvn install -DskipTests;
   cd /root/demos;
done

