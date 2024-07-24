python3 -m pip install virtualenv
python3 -m virtualenv chisel_nb_env
source chisel_nb_env/bin/activate
pip3 install jupyterlab
curl -L -o coursier https://git.io/coursier-cli && chmod +x coursier
SCALA_VERSION=2.13.10 ALMOND_VERSION=0.13.14
./coursier bootstrap -r jitpack \
    -i user -I user:sh.almond:scala-kernel-api_$SCALA_VERSION:$ALMOND_VERSION \
    sh.almond:scala-kernel_$SCALA_VERSION:$ALMOND_VERSION \
    --sources --default=true \
    -o almond
./almond --install
rm almond
rm coursier

echo 'To enter virtualenv type: source chisel_nb_env/bin/activate'
