package bean;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;

import javax.annotation.PreDestroy;
import java.util.function.Function;

public class DockerContainer {
    private final DockerClient dockerClient;
    private final String containerId;

    public DockerContainer(DockerClient dockerClient, Function<DockerClient, CreateContainerResponse> createContainerFunction){
        this.dockerClient=dockerClient;
        this.containerId= createContainerFunction.apply(dockerClient).getId();

        startContainer();
    }

    private void startContainer(){
        dockerClient.startContainerCmd(containerId).exec();
    }

    @PreDestroy
    public void stopAndRemoveContainer(){
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();

    }
}
